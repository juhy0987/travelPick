package com.base.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.base.demo.dto.LocationDto;
import com.base.demo.entity.Location;
import com.base.demo.repository.LocationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocationService {
  @Autowired
  private LocationRepository locationRepository;
  
  public LocationDto getLocation(Integer id) {
    Location location = locationRepository.findById(id).orElse(null);
    if (location == null) {
      return null;
    }
    return location.toLocationDto();
  }
}
