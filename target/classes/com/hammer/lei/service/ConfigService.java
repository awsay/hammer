package com.hammer.lei.service;


import com.hammer.lei.dao.ConfigDao;
import com.hammer.lei.domain.config.WechatConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigService {

  @Autowired
  ConfigDao configDao;

  public List<WechatConfig> getConfigList(){
    return configDao.getConfigList();
  }

  public List<WechatConfig> getOnDutyConfigList(){
    return configDao.getOnDutyConfigList();
  }

  public WechatConfig getConfigById(long id){
    return configDao.getConfigById(id);
  }

  public void saveWechatConfig(WechatConfig wechatConfig){
    if(wechatConfig.getId() == 0){
      configDao.insertConfig(wechatConfig);
    }else{
      configDao.updateConfig(wechatConfig);
    }
  }

  public void delConfig(long id){
    configDao.delConfig(id);
  }
}
