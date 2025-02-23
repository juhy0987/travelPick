package com.base.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<String> authenticationExceptionHandler(AuthenticationException e) {
    System.out.println("authenticationExceptionHandler" + e.toString());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: " + e.getMessage());
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<String> userAlreadyExistsExceptionHandler(UserAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body("Data Conflict: " + e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> illegalArgumentExceptionHandler(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: " + e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> globalExceptionHandler(Exception e) {
    log.error(e.toString());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
  }
}
