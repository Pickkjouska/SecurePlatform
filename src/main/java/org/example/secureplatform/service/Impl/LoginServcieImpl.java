package org.example.secureplatform.service.Impl;

import org.example.secureplatform.common.JwtUtil;
import org.example.secureplatform.common.RedisCache;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.LoginUser;
import org.example.secureplatform.entity.User;
import org.example.secureplatform.service.LoginServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Objects;

@Service
public class LoginServcieImpl extends LoginServcie {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public ResponseResult login(User user) {
        //创建了一个包含用户名和密码的 UsernamePasswordAuthenticationToken 对象。
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        //使用 AuthenticationManager 的 authenticate 方法来验证传入的用户名和密码是否正确。
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //使用userid生成token，Jwt
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);

        //authenticate存入redis
        redisCache.setCacheObject("user"+userId,loginUser);
        String redisKey = "user" + userId;
        System.out.println("LoginredisKey = " + redisKey);
        LoginUser loginUsertext = redisCache.getCacheObject(redisKey);
        System.out.println("Login-loginUser = " + loginUsertext);
        //把token响应给前端
        HashMap<String,String> map = new HashMap<>();
        map.put("token",jwt);
        return new ResponseResult(200,"登陆成功",map);
    }

    @Override
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        redisCache.deleteObject("login:"+userid);
        return new ResponseResult(200,"退出成功");
    }

    @Override
    public ResponseResult register(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("username: " + user.getUsername());
        System.out.println("password: " + encryptedPassword);

        return new ResponseResult(200,"登陆成功",encryptedPassword);
    }
}

