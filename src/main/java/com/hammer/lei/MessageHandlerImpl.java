package com.hammer.lei;

import com.hammer.lei.domain.config.WechatConfig;
import com.hammer.lei.domain.shared.*;
import com.hammer.lei.service.*;
import com.hammer.lei.utils.MessageUtils;
import com.hammer.lei.utils.ThreadExecutorUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MessageHandlerImpl implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandlerImpl.class);
    @Autowired
    private WechatHttpService wechatHttpService;
    private static Set<String> keySet = new HashSet();

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private LoginService loginService;

    private String [] listenChatRooms = null;

    private Map<String,WechatConfig> onCalls = null;

    final static Cache<String, String> cache = CacheBuilder.newBuilder()
        //设置cache的初始大小为10，要合理设置该值
        .initialCapacity(10)
        //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
        .concurrencyLevel(5)
        //设置cache中的数据在写入之后的存活时间为10分钟
        .expireAfterWrite(10, TimeUnit.MINUTES)
//        .expireAfterAccess(10,TimeUnit.MINUTES)
        //构建cache实例
        .build();

    final static Cache<String, String> cache5 = CacheBuilder.newBuilder()
        //设置cache的初始大小为10，要合理设置该值
        .initialCapacity(10)
        //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
        .concurrencyLevel(5)
        //设置cache中的数据在写入之后的存活时间为10分钟
        .expireAfterWrite(5, TimeUnit.MINUTES)
//        .expireAfterAccess(10,TimeUnit.MINUTES)
        //构建cache实例
        .build();

    @Override
    public void onReceivingChatRoomTextMessage(Message message) {
        logger.info("onReceivingChatRoomTextMessage");
        logger.info("from chatroom: " + message.getFromUserName());
        logger.info("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
        logger.info("to: " + message.getToUserName());
        String content = MessageUtils.getChatRoomTextMessageContent(message.getContent());
        logger.info("content:" + content);
        Set<Contact> contacts = cacheService.getChatRooms();
        if(StringUtils.isAnyEmpty(listenChatRooms)){
            loginService.setListenConfig();
        }
        if(StringUtils.isNoneEmpty(listenChatRooms) && ArrayUtils.contains(listenChatRooms,message.getFromUserName()) && MapUtils.isNotEmpty(onCalls)){
            WechatConfig wechatConfig = onCalls.get(message.getFromUserName());
            if(content.contains(wechatConfig.getStopCallKeyWords()) ){
                cache.put(message.getFromUserName(),message.getContent().substring(0,message.getContent().indexOf(":")));
            }else if(cache5.getIfPresent(message.getFromUserName())!= null){
                return;
            }
            if(cache.getIfPresent(message.getFromUserName()) != null){
                if(message.getContent().startsWith(String.valueOf(cache.getIfPresent(message.getFromUserName())))){
                    //依然是回复关键词的人在回复消息，那么刷新缓存
                    cache.put(message.getFromUserName(),message.getContent().substring(0,message.getContent().indexOf(":")));
                }
                return;
            }
            String key = wechatConfig.getOnCall1Name()+"_"+wechatConfig.getOnCall1Phone();
            if (!keySet.contains(key)) {
                keySet.add(key);
                cache5.put(message.getFromUserName(),"1");
                ThreadExecutorUtil.executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            message.setContent("[群聊机器人自动回复]：" + content + "\n电话"+wechatConfig.getOnCall1Name()+"中。。。");
                            replyMessage(message);
                            twilioService.callSomebody(wechatConfig.getOnCall1Phone());
                        }catch (Exception e){
                            logger.error("ChatRoom replyMessage error!", e);
                        }
                        keySet.remove(key);
                    }
                });
            }
        }
    }

    @Override
    public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
        logger.info("onReceivingChatRoomImageMessage");
        logger.info("thumbImageUrl:" + thumbImageUrl);
        logger.info("fullImageUrl:" + fullImageUrl);
    }

    @Override
    public void onReceivingPrivateTextMessage(Message message) throws IOException {
        logger.info("onReceivingPrivateTextMessage");
        logger.info("from: " + message.getFromUserName());
        logger.info("to: " + message.getToUserName());
        logger.info("content:" + message.getContent());
//        将原文回复给对方
        replyMessage(message);
    }

    @Override
    public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) throws IOException {
        logger.info("onReceivingPrivateImageMessage");
        logger.info("thumbImageUrl:" + thumbImageUrl);
        logger.info("fullImageUrl:" + fullImageUrl);
//        将图片保存在本地
        byte[] data = wechatHttpService.downloadImage(thumbImageUrl);
        FileOutputStream fos = new FileOutputStream("thumb.jpg");
        fos.write(data);
        fos.close();
    }

    @Override
    public boolean onReceivingFriendInvitation(RecommendInfo info) {
        logger.info("onReceivingFriendInvitation");
        logger.info("recommendinfo content:" + info.getContent());
//        默认接收所有的邀请
        return true;
    }

    @Override
    public void postAcceptFriendInvitation(Message message) throws IOException {
        logger.info("postAcceptFriendInvitation");
//        将该用户的微信号设置成他的昵称
        String content = StringEscapeUtils.unescapeXml(message.getContent());
        ObjectMapper xmlMapper = new XmlMapper();
        FriendInvitationContent friendInvitationContent = xmlMapper.readValue(content, FriendInvitationContent.class);
        wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), friendInvitationContent.getFromusername());
    }

    @Override
    public void onChatRoomMembersChanged(Contact chatRoom, Set<ChatRoomMember> membersJoined, Set<ChatRoomMember> membersLeft) {
        logger.info("onChatRoomMembersChanged");
        logger.info("chatRoom:" + chatRoom.getUserName());
        if (membersJoined != null && membersJoined.size() > 0) {
            logger.info("membersJoined:" + String.join(",", membersJoined.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
        }
        if (membersLeft != null && membersLeft.size() > 0) {
            logger.info("membersLeft:" + String.join(",", membersLeft.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
        }
    }

    @Override
    public void onNewChatRoomsFound(Set<Contact> chatRooms) {
        logger.info("onNewChatRoomsFound");
        chatRooms.forEach(x -> logger.info(x.getUserName()));
    }

    @Override
    public void onChatRoomsDeleted(Set<Contact> chatRooms) {
        logger.info("onChatRoomsDeleted");
        chatRooms.forEach(x -> logger.info(x.getUserName()));
    }

    @Override
    public void onNewFriendsFound(Set<Contact> contacts) {
        logger.info("onNewFriendsFound");
        contacts.forEach(x -> {
            logger.info(x.getUserName());
            logger.info(x.getNickName());
        });
    }

    @Override
    public void onFriendsDeleted(Set<Contact> contacts) {
        logger.info("onFriendsDeleted");
        contacts.forEach(x -> {
            logger.info(x.getUserName());
            logger.info(x.getNickName());
        });
    }

    @Override
    public void onNewMediaPlatformsFound(Set<Contact> mps) {
        logger.info("onNewMediaPlatformsFound");
    }

    @Override
    public void onMediaPlatformsDeleted(Set<Contact> mps) {
        logger.info("onMediaPlatformsDeleted");
    }

    @Override
    public void onRedPacketReceived(Contact contact) {
        logger.info("onRedPacketReceived");
        if (contact != null) {
            logger.info("the red packet is from " + contact.getNickName());
        }
    }

    private void replyMessage(Message message) throws IOException {
        wechatHttpService.sendText(message.getFromUserName(), message.getContent());
    }

    private void replyChatRoomMessage(Message message) throws IOException {
        wechatHttpService.sendText(message.getToUserName(), message.getContent());
    }


    public String[] getListenChatRooms() {
        return listenChatRooms;
    }

    public void setListenChatRooms(String[] listenChatRooms) {
        this.listenChatRooms = listenChatRooms;
    }

    public Map<String,WechatConfig> getOnCalls() {
        return onCalls;
    }

    public void setOnCalls(Map<String,WechatConfig> onCalls) {
        this.onCalls = onCalls;
    }
}
