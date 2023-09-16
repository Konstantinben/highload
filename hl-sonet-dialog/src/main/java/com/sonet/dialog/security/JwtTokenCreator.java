package com.sonet.dialog.security;

import com.sonet.dialog.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenCreator {

    @Value("${app.jwt.header}")
    private String authorizationHeader;

    @Value("${app.jwt.secret}")
    private String secretKey;
    @Value("${app.jwt.expiration}")
    private long validityMs;

    public String createToken(String username, Role role, UUID uuid) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        claims.put("uuid", uuid);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }
}
