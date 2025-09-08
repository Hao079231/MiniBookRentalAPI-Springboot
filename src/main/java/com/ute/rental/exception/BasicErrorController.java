package com.ute.rental.exception;

import com.ute.rental.dto.ApiMessageDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("customErrorController")
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController {
  @GetMapping
  public ResponseEntity<ApiMessageDto<String>> error(HttpServletRequest request) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    return new ResponseEntity<>(apiMessageDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
