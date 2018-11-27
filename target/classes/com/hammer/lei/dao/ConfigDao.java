package com.hammer.lei.dao;

import com.hammer.lei.domain.config.WechatConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by leifeifei
 */
@Mapper
public interface ConfigDao {

  @Select("select * from wechat_listen_config")
  List<WechatConfig> getConfigList();

  @Select("select * from wechat_listen_config where on_duty_start_date<now() and on_duty_end_date>now()")
  List<WechatConfig> getOnDutyConfigList();

  @Select("select * from wechat_listen_config where id=#{id}")
  WechatConfig getConfigById(long id);

  @Insert("insert into wechat_listen_config (chatroom_nick_name, stop_call_key_words, on_call1_phone, on_call1_name, on_duty_start_date, on_duty_end_date, client_ip) " +
      "values (#{chatroomNickName},#{stopCallKeyWords},#{onCall1Phone},#{onCall1Name},#{onDutyStartDate},#{onDutyEndDate},#{clientIp})")
  void insertConfig(WechatConfig WechatConfig);

  @Update("update wechat_listen_config set chatroom_nick_name=#{chatroomNickName},stop_call_key_words=#{stopCallKeyWords}," +
      "on_call1_phone=#{onCall1Phone},on_call1_name=#{onCall1Name}, on_duty_start_date=#{onDutyStartDate}, " +
      "on_duty_end_date=#{onDutyEndDate}, client_ip=#{clientIp} where id=#{id}")
  void updateConfig(WechatConfig WechatConfig);


  @Delete("delete from wechat_listen_config where id=#{id}")
  void delConfig(long id);
}
