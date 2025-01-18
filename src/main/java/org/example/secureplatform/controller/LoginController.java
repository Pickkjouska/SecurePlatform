package org.example.secureplatform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.User;
import org.example.secureplatform.service.LoginServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginServcie loginServcie;

    @PostMapping("/api/login")
    private ResponseResult login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response){
        return loginServcie.login(request, response, user);
    }

}


