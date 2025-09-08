package com.ute.rental.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceConfig {
  @Autowired
  CustomJwtDecoder customJwtDecoder;

  @Autowired
  SecurityConfig securityConfig;

  private final String[] PUBLIC_ENDPOINTS = {
      "/api/token", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**",
      "/api-docs/**"
  };

  @Bean
  public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // Các endpoint công khai
            .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
            .anyRequest().authenticated());

    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
            .decoder(customJwtDecoder)
            .jwtAuthenticationConverter(securityConfig.jwtAuthenticationConverter()))
        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
    http.csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
