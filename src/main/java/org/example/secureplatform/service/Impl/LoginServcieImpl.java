package org.example.secureplatform.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.secureplatform.common.util.JwtUtil;
import org.example.secureplatform.common.RedisCache;
import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.GeoLocationResponse;
import org.example.secureplatform.entity.User;
import org.example.secureplatform.service.LoginServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.secureplatform.entity.LoginDetails;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
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
    private static final String API_URL = "http://ip-api.com/json/";

    @Override
    public ResponseResult login(HttpServletRequest request, HttpServletResponse response, User user) {
        //创建了一个包含用户名和密码的 UsernamePasswordAuthenticationToken 对象。
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        //使用 AuthenticationManager 的 authenticate 方法来验证传入的用户名和密码是否正确。
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误s");
        }
        String jwt = JwtUtil.createJWT(user.getUsername());
        HashMap<String,String> map = new HashMap<>();
        map.put("token",jwt);
        loginDratil(request, response, "true");
        map.put("loginIp", LoginDetails.getIp());
        map.put("loginUserAgent", LoginDetails.getUserAgent());
        map.put("loginTime", LoginDetails.getLoginTime().toString());
        map.put("status", LoginDetails.getStatus());
        map.put("local", LoginDetails.getLocation());
        return new ResponseResult(200,"登陆成功",map);
    }

    public void loginDratil(HttpServletRequest request, HttpServletResponse response, String status) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime loginTime = LocalDateTime.now();
        String location = getGeoLocation(ip);
        LoginDetails.setIp(ip);
        LoginDetails.setUserAgent(userAgent);
        LoginDetails.setLoginTime(loginTime);
        LoginDetails.setStatus(status);
        LoginDetails.setLocation(location);
    }
    public String getGeoLocation(String ip) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL;
        try {
            GeoLocationResponse response = restTemplate.getForObject(url, GeoLocationResponse.class);
            if (response != null && "success".equals(response.getStatus())) {
                return response.getCity() + ", " + response.getCountry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown location";
    }
}

