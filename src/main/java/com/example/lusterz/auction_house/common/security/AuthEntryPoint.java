package com.example.lusterz.auction_house.common.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.common.exception.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint{

    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        
        // set the header
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorDetails errorDetails = new ErrorDetails(
            HttpServletResponse.SC_UNAUTHORIZED,
            "Unauthorized",
            authException.getMessage(),
            request.getServletPath()
        );

        mapper.writeValue(response.getOutputStream(), errorDetails);
    }
    
}
