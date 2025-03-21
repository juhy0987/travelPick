package com.base.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.base.demo.dto.LoginDto;
import com.base.demo.dto.UserRegisterDto;
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
    User user = userService.authenticate(loginDto);
    session.setAttribute("user", user);
    
    return ResponseEntity.status(HttpStatus.OK).body("Login successful");
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(
    @RequestBody UserRegisterDto registerDto,
    HttpSession session) {
    User user = userService.register(registerDto);
    session.setAttribute("user", user);
    
    return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
  }

  @GetMapping("")
  public ResponseEntity<String> check(HttpSession session) {
    return ResponseEntity.status(HttpStatus.OK).body("Authorized");
  }

  @DeleteMapping("")
  public ResponseEntity<String> logout(HttpSession session) {
    session.invalidate();
    return ResponseEntity.status(HttpStatus.OK).body("Logged out");
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> delete(HttpSession session) {
    User user = (User) session.getAttribute("user");
    
    userService.delete(user);
    session.invalidate();
    return ResponseEntity.status(HttpStatus.OK).body("User deleted");
  }
}
