package com.example.lusterz.auction_house.common.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.common.exception.AuthException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secretKey;
    @Value("${app.jwt.access.expiration}")
    private Long jwtExpiration;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getKey())
                .compact();
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getKey())
                .compact();
    }

    public String getIdentifierFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            throw AuthException.JwtToken.malformed();
        } catch (SignatureException e) {
            throw AuthException.JwtToken.invalidSignature();
        } catch (ExpiredJwtException e) {
            throw AuthException.JwtToken.expired();
        }
         catch (Exception e) {
            throw new AuthException.JwtToken("Authentication failed" + e.getMessage());
        }
    }

    private SecretKey getKey() {
        byte[] keyBytes =  Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long getJwtExpiration() {
        return this.jwtExpiration;
    }
}
