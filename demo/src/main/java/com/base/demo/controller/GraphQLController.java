package com.base.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.base.demo.dto.AutoCompleteDto;
import com.base.demo.entity.Resort;
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
  private ReviewService reviewService;
  @Autowired
  private PhotoService photoService;
  
  @QueryMapping
  public String hello() {
    return "Hello, GraphQL!";
  }

  @QueryMapping
  public Resort getResort(UUID id) {
    Resort resort = resortService.getResort(id);
    return resort;
  }

  // @QueryMapping
  // public List<Resort> search() {
  //   return resortService.getAll();
  // }

  @QueryMapping
  public List<AutoCompleteDto> autoComplete(@Argument String query) {
    List<AutoCompleteDto> tmp = resortService.autoComplete(query);
    System.out.println(tmp);
    return tmp;
  }
}
