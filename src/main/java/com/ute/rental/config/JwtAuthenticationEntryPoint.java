package com.ute.rental.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    // Tạo instance của ApiMessageDto
    ApiMessageDto<Object> apiMessageDto = new ApiMessageDto<>();
    apiMessageDto.setCode(ErrorCode.TOKEN_ERROR_INVALID);
    apiMessageDto.setMessage("Authentication failed: Invalid or missing token");

    ObjectMapper objectMapper = new ObjectMapper();

    // Ghi phản hồi JSON vào response
    response.getWriter().write(objectMapper.writeValueAsString(apiMessageDto));
    response.flushBuffer();
  }
}