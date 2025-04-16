package com.base.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.base.demo.dto.LocationDto;
import com.base.demo.service.LocationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LocationGraphQLController {
  @Autowired
  private LocationService locationService;

  @QueryMapping
  public LocationDto getLocation(@Argument Integer id) {
    return locationService.getLocation(id);
  }
}
