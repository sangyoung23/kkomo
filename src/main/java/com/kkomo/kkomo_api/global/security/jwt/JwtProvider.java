package com.kkomo.kkomo_api.global.security.jwt;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

@Component
public class JwtProvider {

    private final String secretKey = "secret";

    public String createToken(Long userId) {

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 3600000)
                )
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Long getUserId(String token) {

        Claims claims =
                Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token)
                        .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
