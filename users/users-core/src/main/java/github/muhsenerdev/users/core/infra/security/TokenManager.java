package github.muhsenerdev.users.core.infra.security;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import github.muhsenerdev.users.core.infra.config.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private final SecurityProperties securityProperties;

    public String generateToken(Map<String, Object> claims, String subject) {
        SecretKey key = Keys.hmacShaKeyFor(securityProperties.getJwt().getSecret().getBytes());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + securityProperties.getJwt().getExpiration()))
                .signWith(key)
                .compact();
    }
}
