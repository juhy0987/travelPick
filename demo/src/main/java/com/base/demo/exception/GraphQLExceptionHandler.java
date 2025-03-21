package com.base.demo.exception;

import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.base.demo.controller.GraphQLController;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Hidden
@ControllerAdvice(assignableTypes = {GraphQLController.class})
public class GraphQLExceptionHandler {
  @ExceptionHandler(AuthenticationException.class)
  public GraphQLError authenticationExceptionHandler(AuthenticationException e) {
    return GraphqlErrorBuilder.newError()
      .message("Unauthorized")
      .extensions(Map.of("code", "UNAUTHORIZED"))
      .build();
  }

  @ExceptionHandler(NullPointerException.class)
  public GraphQLError nullPointerExceptionHandler(NullPointerException e) {
    return GraphqlErrorBuilder.newError()
      .message("Not Found")
      .extensions(Map.of("code", "NOT_FOUND"))
      .build();
  }

  @ExceptionHandler(Exception.class)
    public GraphQLError handleGenericException(Exception e) {
      return GraphqlErrorBuilder.newError()
        .message("Internal Server Error")
        .extensions(Map.of("code", "INTERNAL_ERROR"))
        .build();
  }
}