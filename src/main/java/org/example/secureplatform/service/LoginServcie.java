package org.example.secureplatform.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public abstract class LoginServcie {
    public abstract ResponseResult login(HttpServletRequest request, HttpServletResponse response, User user);

}
