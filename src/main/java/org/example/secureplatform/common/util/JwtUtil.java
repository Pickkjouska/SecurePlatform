package org.example.secureplatform.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 */
public class JwtUtil {

    // 有效期为12个小时
    public static final Long JWT_TTL = 12 * 60 * 60 * 1000L;
    // 设置秘钥明文
    public static final String JWT_KEY = "PickkAtaraxiaPickkAtaraxiaPickkAtaraxiaPickkAtaraxia";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成JWT
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        return createJWT(subject, JWT_TTL);
    }

    /**
     * 生成JWT
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        return getJwtBuilder(subject, ttlMillis, getUUID()).compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        Date now = new Date();
        if (ttlMillis == null) {
            ttlMillis = JWT_TTL;
        }
        Date expDate = new Date(now.getTime() + ttlMillis);
        return Jwts.builder()
                .setId(uuid) // 唯一的ID
                .setSubject(subject) // 主题 可以是JSON数据
                .setIssuer("Pickk") // 签发者
                .setIssuedAt(now) // 签发时间
                .signWith(signatureAlgorithm, secretKey) // 使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);
    }

    /**
     * 创建token
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        return getJwtBuilder(subject, ttlMillis, id).compact();
    }

    public static void main(String[] args) throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5ZjAxNWVjOTJlNzM0MzA1YmU0NWExN2FhN2RlYzRjYyIsInN1YiI6ImFkbWluIiwiaXNzIjoiUGlja2siLCJpYXQiOjE3MzU3Mjg2MTEsImV4cCI6MTczNTgxNTAxMX0.ibMiPmr7X244E3HKEKMODSdnk-JASRn63dYcVzBixno";
        System.out.println(parseJWT(token));
    }

    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
    }

    /**
     * 解析JWT
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}