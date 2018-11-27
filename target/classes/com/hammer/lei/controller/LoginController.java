package com.hammer.lei.controller;

import com.hammer.lei.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by leifeifei
 */

@Controller
public class LoginController {

  @Autowired
  LoginService loginService;

  @RequestMapping(value = "/login", method = {RequestMethod.GET})
  public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
    ModelAndView mv = new ModelAndView("login");
    if(LoginService.isLogin){
      mv = new ModelAndView("redirect:toConfigPage");
      return mv;
    }
    mv.addObject("base64Img",LoginService.QR_CODE_BASE64);
    mv.addObject("version",LoginService.VERSION);
    return mv;
  }

  @RequestMapping(value = "/loginStatus", method = {RequestMethod.POST})
  @ResponseBody
  public String loginStatus(@RequestParam(value = "version") int version){
    if(LoginService.isLogin){
      return "LOGIN_SUCCESS";
    }
    if(LoginService.VERSION > version){
      return "REFRESH";
    }
    return "";
  }
}
