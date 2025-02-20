package com.base.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.base.demo.repository.LocationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocationService {
  @Autowired
  private LocationRepository locationRepository;
}
