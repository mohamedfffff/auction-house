package com.example.lusterz.auction_house.common.exception;

import com.example.lusterz.auction_house.common.exception.AuthException.JwtToken;

public class AuthException extends RuntimeException{
    public AuthException(String message) {
        super(message);
    }

    public static class JwtToken extends AuthException{
        public JwtToken(String message) {
            super(message);
        }

        public static JwtToken malformed() {
            return new JwtToken("Invalid token format");
        }
        public static JwtToken invalidSignature() {
            return new JwtToken("Security Signature mismatch");
        }
        public static JwtToken expired() {
            return new JwtToken("Session expired. Please login again");
        }
    }

    public static class RefreshToken extends AuthException{
        public RefreshToken(String message) {
            super(message);
        }

        public static RefreshToken expired() {
            return new RefreshToken("Session expired. Please login again");
        }
    }

    public static class Unauthorized extends AuthException{
        public Unauthorized(String message) {
            super(message);
        }

        public static Unauthorized wrongPassword() {
            return new Unauthorized("The password is incorrect");
        }
    }

    public static class NotFound extends AuthException{
        public NotFound(String message) {
            super(message);
        }

        public static NotFound refreshToken() {
            return new NotFound("Refresh token not found");
        }
    }
}
