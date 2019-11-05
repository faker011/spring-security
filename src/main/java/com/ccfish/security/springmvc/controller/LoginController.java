package com.ccfish.security.springmvc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Ciaos
 * @Date: 2019/11/5 21:32
 */
@RestController
public class LoginController {
    @RequestMapping(value="/login-success", produces = {"text/plain;charset=UTF-8"})
    public String loginSuccess(){
        return "登录成功";
    }
    @RequestMapping(value="/r/r1", produces = {"text/plain;charset=UTF-8"})
    public String r1(){
        return "访问资源R1";
    }
    @RequestMapping(value="/r/r2", produces = {"text/plain;charset=UTF-8"})
    public String r2(){
        return "访问资源R2";
    }
    @RequestMapping(value="/s/s1", produces = {"text/plain;charset=UTF-8"})
    public String s1(){
        return "不需要授权资源S1";
    }
}
