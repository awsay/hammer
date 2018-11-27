package com.hammer.lei.domain.config;

import java.util.Date;

/**
 * Created by leifeifei
 */

public class WechatConfig {

  private long id;
  private String chatroomNickName; //微信群名称
  private String stopCallKeyWords; //监听关键词，出现该关键词，就持续监听该人消息，该发言人只要言就不打电话，且10分钟后非该发言人发言才会打电话
  private String onCall1Phone;  //值班人电话
  private String onCall1Name; //值班人姓名
  private Date onDutyStartDate; //值班开始日期
  private Date onDutyEndDate; //值班结束日期
  private Date createDate; //创建时间
  private Date updateTime; //更新时间
  private String clientIp; //更新IP
  private int version; //乐观锁

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getChatroomNickName() {
    return chatroomNickName;
  }

  public void setChatroomNickName(String chatroomNickName) {
    this.chatroomNickName = chatroomNickName;
  }

  public String getStopCallKeyWords() {
    return stopCallKeyWords;
  }

  public void setStopCallKeyWords(String stopCallKeyWords) {
    this.stopCallKeyWords = stopCallKeyWords;
  }

  public String getOnCall1Phone() {
    return onCall1Phone;
  }

  public void setOnCall1Phone(String onCall1Phone) {
    this.onCall1Phone = onCall1Phone;
  }

  public String getOnCall1Name() {
    return onCall1Name;
  }

  public void setOnCall1Name(String onCall1Name) {
    this.onCall1Name = onCall1Name;
  }

  public Date getOnDutyStartDate() {
    return onDutyStartDate;
  }

  public void setOnDutyStartDate(Date onDutyStartDate) {
    this.onDutyStartDate = onDutyStartDate;
  }

  public Date getOnDutyEndDate() {
    return onDutyEndDate;
  }

  public void setOnDutyEndDate(Date onDutyEndDate) {
    this.onDutyEndDate = onDutyEndDate;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }
}
