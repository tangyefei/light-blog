package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 将字符串密钥转换为 javax.crypto.SecretKey 对象。
     * 这是 jjwt 0.12.x 版本要求的签名方式，必须用 Key 对象而非直接传字符串。
     *
     * @return HMAC-SHA 算法专用的 SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    /**
     * 生成 JWT Token，通常用于登录成功后返回给前端。
     *
     * @param userId   用户ID（数据库主键）
     * @param username 用户名（可选，用于展示，但不要放敏感信息）
     * @return 完整的 JWT 字符串，格式：xxxxx.yyyyy.zzzzz
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims passToken(String token) {
        // Jwts.parser()：创建一个新的 JWT 解析器
        // .verifyWith(key)：设置验证签名所用的密钥
        // .build()：构建解析器实例
        // .parseSignedClaims(token)：解析已签名的 JWT，返回 Jws<Claims> 对象
        // .getPayload()：获取其中的 Claims（即第二部分 Payload 的内容）
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = passToken(token);
        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            passToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
