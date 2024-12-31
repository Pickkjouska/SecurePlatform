package org.example.secureplatform.service;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.User;
import org.springframework.stereotype.Service;

@Service
public abstract class LoginServcie {
    public abstract ResponseResult login(User user);
}
