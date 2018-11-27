package com.hammer.lei.controller;

import com.hammer.lei.constant.StringConstants;
import com.hammer.lei.domain.config.WechatConfig;
import com.hammer.lei.service.CacheService;
import com.hammer.lei.service.ConfigService;
import com.hammer.lei.service.LoginService;
import com.hammer.lei.utils.ApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by leifeifei
 */

@Controller
public class ConfigController {

  @Autowired
  LoginService loginService;

  @Autowired
  CacheService cacheService;

  @Autowired
  ConfigService configService;

  @RequestMapping(value = "toConfigPage",method = {RequestMethod.GET})
  public ModelAndView toConfigPage(){
    ModelAndView mv = new ModelAndView("config");
    if(!LoginService.isLogin){
      mv = new ModelAndView("redirect:login");
      return mv;
    }
    mv.addObject("configList",configService.getConfigList());
//    mv.addObject("chatRooms",cacheService.getChatRooms());
    return mv;
  }

  @RequestMapping(value = "editConfig",method = {RequestMethod.GET})
  public ModelAndView editConfig(@RequestParam(value = "id",required = false,defaultValue = "0") long id){
    ModelAndView mv = new ModelAndView("edit_config");
    if(!LoginService.isLogin){
      mv = new ModelAndView("redirect:login");
      return mv;
    }
    mv.addObject("chatRooms",cacheService.getChatRooms());
    if(id>0){
      mv.addObject("config",configService.getConfigById(id));
    }
    return mv;
  }


  @RequestMapping(value = "saveConfig",method = {RequestMethod.POST})
  @ResponseBody
  public String saveConfig(@ModelAttribute WechatConfig wechatConfig, HttpServletRequest request) {
    if(!LoginService.isLogin){
      return "登录已失效，请重新登录！";
    }
    wechatConfig.setClientIp(ApiUtil.getClientIP(request));
    configService.saveWechatConfig(wechatConfig);
    loginService.setListenConfig();
    return StringConstants.SUCCESS;
  }

  @RequestMapping(value = "delConfig",method = {RequestMethod.GET})
  @ResponseBody
  public String delConfig(@RequestParam(value = "id") long id){
    if(!LoginService.isLogin){
      return "登录失效";
    }
    if(id>0){
      configService.delConfig(id);
    }
    return StringConstants.SUCCESS;
  }

}
