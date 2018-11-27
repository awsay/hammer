package com.hammer.lei.service;

import com.hammer.lei.MessageHandlerImpl;
import com.hammer.lei.domain.config.WechatConfig;
import com.hammer.lei.domain.request.component.BaseRequest;
import com.hammer.lei.domain.response.*;
import com.hammer.lei.domain.shared.ChatRoomDescription;
import com.hammer.lei.domain.shared.Contact;
import com.hammer.lei.domain.shared.Token;
import com.hammer.lei.enums.LoginCode;
import com.hammer.lei.enums.StatusNotifyCode;
import com.hammer.lei.exception.WechatException;
import com.hammer.lei.exception.WechatQRExpiredException;
import com.hammer.lei.utils.QRCodeUtils;
import com.hammer.lei.utils.WechatUtils;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private CacheService cacheService;
    @Autowired
    private SyncServie syncServie;
    @Autowired
    private WechatHttpServiceInternal wechatHttpServiceInternal;

    public static String QR_CODE_BASE64 = "";

    public static int VERSION = 0;

    public static boolean isLogin = false;

    @Value("${hammer.auto-relogin-when-qrcode-expired}")
    private boolean AUTO_RELOGIN_WHEN_QRCODE_EXPIRED;

    @Value("${hammer.max-qr-refresh-times}")
    private int MAX_QR_REFRESH_TIMES;

    private int qrRefreshTimes = 0;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MessageHandlerImpl messageHandler;

    public void login() {
        try {
            //0 entry
            wechatHttpServiceInternal.open(qrRefreshTimes);
            logger.info("[0] entry completed");
            //1 uuid
            String uuid = wechatHttpServiceInternal.getUUID();
            cacheService.setUuid(uuid);
            logger.info("[1] uuid completed");
            //2 qr
            byte[] qrData = wechatHttpServiceInternal.getQR(uuid);
            ByteArrayInputStream stream = new ByteArrayInputStream(qrData);
            String qrUrl = QRCodeUtils.decode(stream);
            stream.close();
//            String qr = QRCodeUtils.generateQR(qrUrl, 40, 40);
            QR_CODE_BASE64 = QRCodeUtils.generateQRBase64(qrUrl, 400, 400);
            VERSION++;
            isLogin = false;
//            logger.info("\r\n" + qr);
            logger.info("[2] qrcode completed");
            //3 statreport
            wechatHttpServiceInternal.statReport();
            logger.info("[3] statReport completed");
            //4 login
            LoginResult loginResponse;
            while (true) {
                loginResponse = wechatHttpServiceInternal.login(uuid);
                if (LoginCode.SUCCESS.getCode().equals(loginResponse.getCode())) {
                    if (loginResponse.getHostUrl() == null) {
                        throw new WechatException("hostUrl can't be found");
                    }
                    if (loginResponse.getRedirectUrl() == null) {
                        throw new WechatException("redirectUrl can't be found");
                    }
                    cacheService.setHostUrl(loginResponse.getHostUrl());
                    if (loginResponse.getHostUrl().equals("https://wechat.com")) {
                        cacheService.setSyncUrl("https://webpush.web.wechat.com");
                        cacheService.setFileUrl("https://file.web.wechat.com");
                    } else {
                        cacheService.setSyncUrl(loginResponse.getHostUrl().replaceFirst("^https://", "https://webpush."));
                        cacheService.setFileUrl(loginResponse.getHostUrl().replaceFirst("^https://", "https://file."));
                    }
                    break;
                } else if (LoginCode.AWAIT_CONFIRMATION.getCode().equals(loginResponse.getCode())) {
                    logger.info("[*] login status = AWAIT_CONFIRMATION");
                } else if (LoginCode.AWAIT_SCANNING.getCode().equals(loginResponse.getCode())) {
                    logger.info("[*] login status = AWAIT_SCANNING");
                } else if (LoginCode.EXPIRED.getCode().equals(loginResponse.getCode())) {
                    logger.info("[*] login status = EXPIRED");
                    throw new WechatQRExpiredException();
                } else {
                    logger.info("[*] login status = " + loginResponse.getCode());
                }
            }
            logger.info("[4] login completed");
            isLogin = true;


            Token token = wechatHttpServiceInternal.openNewloginpage(loginResponse.getRedirectUrl());
            if (token.getRet() == 0) {
                cacheService.setPassTicket(token.getPass_ticket());
                cacheService.setsKey(token.getSkey());
                cacheService.setSid(token.getWxsid());
                cacheService.setUin(token.getWxuin());
                BaseRequest baseRequest = new BaseRequest();
                baseRequest.setUin(cacheService.getUin());
                baseRequest.setSid(cacheService.getSid());
                baseRequest.setSkey(cacheService.getsKey());
                cacheService.setBaseRequest(baseRequest);
            } else {
                throw new WechatException("token ret = " + token.getRet());
            }
            logger.info("[5] redirect login completed");
            //6 redirect
            wechatHttpServiceInternal.redirect(cacheService.getHostUrl());
            logger.info("[6] redirect completed");
            //7 init
            InitResponse initResponse = wechatHttpServiceInternal.init(cacheService.getHostUrl(), cacheService.getBaseRequest());
            WechatUtils.checkBaseResponse(initResponse);
            cacheService.setSyncKey(initResponse.getSyncKey());
            cacheService.setOwner(initResponse.getUser());
            logger.info("[7] init completed");
            //8 status notify
            StatusNotifyResponse statusNotifyResponse =
                    wechatHttpServiceInternal.statusNotify(cacheService.getHostUrl(),
                            cacheService.getBaseRequest(),
                            cacheService.getOwner().getUserName(), StatusNotifyCode.INITED.getCode());
            WechatUtils.checkBaseResponse(statusNotifyResponse);
            logger.info("[8] status notify completed");
            //9 get contact
            long seq = 0;
            do {
                GetContactResponse getContactResponse = wechatHttpServiceInternal.getContact(cacheService.getHostUrl(), cacheService.getBaseRequest().getSkey(), seq);
                WechatUtils.checkBaseResponse(getContactResponse);
                logger.info("[*] getContactResponse seq = " + getContactResponse.getSeq());
                logger.info("[*] getContactResponse memberCount = " + getContactResponse.getMemberCount());
                seq = getContactResponse.getSeq();
                cacheService.getIndividuals().addAll(getContactResponse.getMemberList().stream().filter(WechatUtils::isIndividual).collect(Collectors.toSet()));
                cacheService.getMediaPlatforms().addAll(getContactResponse.getMemberList().stream().filter(WechatUtils::isMediaPlatform).collect(Collectors.toSet()));
            } while (seq > 0);
            logger.info("[9] get contact completed");
            //10 batch get contact
            ChatRoomDescription[] chatRoomDescriptions = initResponse.getContactList().stream()
                    .filter(x -> x != null && WechatUtils.isChatRoom(x))
                    .map(x -> {
                        ChatRoomDescription description = new ChatRoomDescription();
                        description.setUserName(x.getUserName());
                        return description;
                    })
                    .toArray(ChatRoomDescription[]::new);
            if (chatRoomDescriptions.length > 0) {
                BatchGetContactResponse batchGetContactResponse = wechatHttpServiceInternal.batchGetContact(
                        cacheService.getHostUrl(),
                        cacheService.getBaseRequest(),
                        chatRoomDescriptions);
                WechatUtils.checkBaseResponse(batchGetContactResponse);
                logger.info("[*] batchGetContactResponse count = " + batchGetContactResponse.getCount());
                cacheService.getChatRooms().addAll(batchGetContactResponse.getContactList());
            }
            logger.info("[10] batch get contact completed");
            cacheService.setAlive(true);
            logger.info("[*] login process completed");
            logger.info("[*] start listening");
            setListenConfig();
            while (true) {
                syncServie.listen();
            }
        } catch (IOException | NotFoundException | WriterException | URISyntaxException ex) {
            throw new WechatException(ex);
        } catch (WechatQRExpiredException ex) {
            if (AUTO_RELOGIN_WHEN_QRCODE_EXPIRED && qrRefreshTimes <= MAX_QR_REFRESH_TIMES) {
                login();
            } else {
                throw new WechatException(ex);
            }
        }
    }


    public boolean setListenConfig(){
        try {
            InitResponse initResponse = wechatHttpServiceInternal.init(cacheService.getHostUrl(), cacheService.getBaseRequest());
            ChatRoomDescription[] chatRoomDescriptions = initResponse.getContactList().stream()
                .filter(x -> x != null && WechatUtils.isChatRoom(x))
                .map(x -> {
                    ChatRoomDescription description = new ChatRoomDescription();
                    description.setUserName(x.getUserName());
                    return description;
                })
                .toArray(ChatRoomDescription[]::new);
            if (chatRoomDescriptions.length > 0) {
                BatchGetContactResponse batchGetContactResponse = wechatHttpServiceInternal.batchGetContact(
                    cacheService.getHostUrl(),
                    cacheService.getBaseRequest(),
                    chatRoomDescriptions);
                WechatUtils.checkBaseResponse(batchGetContactResponse);
                logger.info("[*] batchGetContactResponse count = " + batchGetContactResponse.getCount());
                cacheService.getChatRooms().addAll(batchGetContactResponse.getContactList());
            }
        }catch (IOException e){
            logger.error("setListenConfig error:",e);
        }


        Set<Contact> chatRooms = cacheService.getChatRooms();
        List<WechatConfig> configList = configService.getOnDutyConfigList();
        if(CollectionUtils.isEmpty(chatRooms) || CollectionUtils.isEmpty(configList)){
            return false;
        }

        List<String> listenRooms = new ArrayList<>();
        Map<String,WechatConfig> configRooms= configList.stream().collect(Collectors.toMap(WechatConfig::getChatroomNickName,WechatConfig->WechatConfig));
        Map<String,WechatConfig> map = new HashMap<>();
        for (Contact contact : chatRooms) {
            if(configRooms.get(contact.getNickName()) != null){
                listenRooms.add(contact.getUserName());
                map.put(contact.getUserName(),configRooms.get(contact.getNickName()));
            }
        }
        messageHandler.setOnCalls(map);
        if(CollectionUtils.isNotEmpty(listenRooms)){
            String configs [] = listenRooms.stream().toArray(String []::new);
            messageHandler.setListenChatRooms(configs);
        }
        return true;

    }
}
