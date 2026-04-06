package com.base.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.base.demo.dto.LocationDto;
import com.base.demo.dto.LocationRegisterDto;
import com.base.demo.dto.LocationSearchDto;
import com.base.demo.dto.LocationSearchResponseDto;
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

  @QueryMapping
  public List<LocationSearchResponseDto> searchLocations(@Argument LocationSearchDto locationSearchDto) {
    return locationService.searchLocations(locationSearchDto);
  }

  @QueryMapping
  public LocationDto createLocation(@RequestBody LocationRegisterDto locationRegisterDto) {  
    return locationService.createLocation(locationRegisterDto);
  }
  
}
