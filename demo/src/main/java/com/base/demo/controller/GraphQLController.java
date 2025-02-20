package com.base.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.base.demo.service.LocationService;
import com.base.demo.service.PhotoService;
import com.base.demo.service.ResortService;
import com.base.demo.service.ReviewService;
import com.base.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class GraphQLController {
  @Autowired
  private UserService userService;
  @Autowired
  private ResortService resortService;
  @Autowired
  private LocationService locationService;
  @Autowired
  private ReviewService ReviewService;
  @Autowired
  private PhotoService photoService;

  
  @QueryMapping
  public String hello() {
    return "Hello, GraphQL!";
  }
}
