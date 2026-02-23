package com.example.lusterz.auction_house.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.common.exception.AuthException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtils {

    private SecretKey key;
    private Long jwtExpiration;

    public JwtUtils(@Value("${app.jwt.secret}") String jwtSecret,
                    @Value("${app.jwt.expiration}") Long jwtExpiration) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
    }

    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    public String getIdentifierFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            throw AuthException.JwtError.malformed();
        } catch (SignatureException e) {
            throw AuthException.JwtError.invalidSignature();
        } catch (ExpiredJwtException e) {
            throw AuthException.JwtError.expired();
        }
         catch (Exception e) {
            throw new AuthException.JwtError("Authentication failed" + e.getMessage());
        }
    }
}
