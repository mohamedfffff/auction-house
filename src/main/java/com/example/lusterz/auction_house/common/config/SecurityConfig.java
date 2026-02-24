package com.example.lusterz.auction_house.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.lusterz.auction_house.common.security.JwtFilter;
import com.example.lusterz.auction_house.common.util.JwtUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final JwtFilter jwtFilter;
    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtFilter jwtFilter, JwtUtils jwtUtils) {
        this.jwtFilter = jwtFilter;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csfr -> csfr.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .oauth2Login(oauth2 -> oauth2.
                successHandler((request, response, authentication) -> {
                    String token = jwtUtils.generateToken(authentication);
                    response.sendRedirect("/http://localhost:3000/oauth2/redirect?token=" + token);
                })
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
}
