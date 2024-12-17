package org.example.secureplatform.controller;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.User;
import org.example.secureplatform.service.LoginServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginServcie loginServcie;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        System.out.println("user:" + user);
        return loginServcie.login(user);
    }
    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user){
        System.out.println("user:" + user);
        return loginServcie.register(user);
    }
}


