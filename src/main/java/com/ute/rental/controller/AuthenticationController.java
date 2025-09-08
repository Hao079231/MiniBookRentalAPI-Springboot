package com.ute.rental.controller;

import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.AuthenticationDto;
import com.ute.rental.form.AuthenticationForm;
import com.ute.rental.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class AuthenticationController {
  @Autowired
  UserServiceImpl userService;

  @PostMapping("/token")
  public ApiMessageDto<AuthenticationDto> login(@Valid @RequestBody AuthenticationForm request, BindingResult bindingResult){
    return userService.authenticate(request);
  }
}
