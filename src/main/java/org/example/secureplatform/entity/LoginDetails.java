package org.example.secureplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginDetails {
    private static String ip;
    private static String userAgent;
    private static LocalDateTime loginTime;
    private static String status;
    private static String location;
    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        LoginDetails.status = status;
    }

    // Getters and Setters
    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        LoginDetails.ip = ip;
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static void setUserAgent(String userAgent) {
        LoginDetails.userAgent = userAgent;
    }

    public static LocalDateTime getLoginTime() {
        return loginTime;
    }

    public static void setLoginTime(LocalDateTime loginTime) {
        LoginDetails.loginTime = loginTime;
    }
    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        LoginDetails.location = location;
    }
}
