package com.base.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.base.demo.dto.LoginDto;
import com.base.demo.dto.RegisterDto;
import com.base.demo.entity.User;
import com.base.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
  @Autowired
  private UserService userService;

  @PostMapping("/login")
  public ResponseEntity<String> login(
    @RequestBody LoginDto loginDto,
    HttpSession session) {
    try {
      System.out.println("Logging in user");
      User user = userService.authenticate(loginDto);
      session.setAttribute("user", user);
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }
    
    return ResponseEntity.status(HttpStatus.OK).body("Login successful");
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(
    @RequestBody RegisterDto registerDto,
    HttpSession session) {
    try {
      System.out.println("Registering user");
      log.info(registerDto.toString());
      User user = userService.register(registerDto);
      session.setAttribute("user", user);
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
    } catch (Exception e) {
      System.out.println(e.toString());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
    }
    System.out.println("Registration successful");
    return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
  }

  @ExceptionHandler(Exception.class)
  public String globalExceptionHandler(Exception e) {
    return "error";
  }
}
