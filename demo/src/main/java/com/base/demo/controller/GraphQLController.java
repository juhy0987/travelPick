package com.base.demo.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class GraphQLController {
  @QueryMapping
  public String hello() {
    return "Hello, GraphQL!";
  }
}
