package com.hammer.lei.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 电话短信类 国外的免费服务，登录有人机检测验证码，所以需要翻墙
 * Created by leifeifei
 * https://www.twilio.com
 */

@Component
public class TwilioService {

  //https://www.twilio.com上申请的sid和token，可以在Dashboard页面看到
  private final String ACCOUNT_SID = "AC1f21d468109b436228cc8f2ed36cc58c";
  private final String AUTH_TOKEN = "7a05be61696697b6869efcaef706a5ac";

  public String callSomebody(String phoneNumber) throws URISyntaxException {
    System.out.println("call some body start!");

    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

    Call call = Call.creator(
        new PhoneNumber("+86"+phoneNumber),  // To number
        new PhoneNumber("+14322844795"),  // From number

        // Read TwiML at this URL when a call connects (hold music)
        new URI("http://demo.twilio.com/docs/voice.xml")
    ).create();
    System.out.println("call some body end!");
    System.out.println(call.getSid());
    return call.getSid();
  }


  public String sendMessage(String phoneNumber,String content) {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

    Message message = Message
        .creator(new PhoneNumber("+86"+phoneNumber), // to
            new PhoneNumber("+14322844795"), // from
            content)
        .create();

    System.out.println(message.getSid());
    return message.getSid();
  }

}
