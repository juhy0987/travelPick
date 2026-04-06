package com.base.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.FuzzyScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.base.demo.dto.LocationDto;
import com.base.demo.dto.LocationRegisterDto;
import com.base.demo.dto.LocationSearchDto;
import com.base.demo.dto.LocationSearchResponseDto;
import com.base.demo.entity.Location;
import com.base.demo.repository.LocationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocationService {
  @Autowired
  private LocationRepository locationRepository;

  @Value("${app.similarity.minimum}")
  private int minimumSimilarity;
  
  private final FuzzyScore fuzzyScore = new FuzzyScore(java.util.Locale.KOREAN);

  public LocationDto getLocation(Integer id) {
    Location location = locationRepository.findById(id).orElse(null);
    if (location == null) {
      return null;
    }
    return location.toLocationDto();
  }

  public List<LocationSearchResponseDto> searchLocations(LocationSearchDto locationSearchDto) {
    List<Location> locations = locationRepository.findAll();
    return locations.stream()
      .map(location -> {
        return location.toLocationSearchResponseDto(
          fuzzyScore.fuzzyScore(location.getName(), locationSearchDto.getQuery())
        );
      })
      .filter(dto -> dto.getSimilarity() > minimumSimilarity)
      .sorted((r1, r2) -> Integer.compare(r1.getSimilarity(), r2.getSimilarity()))
      .collect(Collectors.toList());
  }

  public LocationDto createLocation(LocationRegisterDto locationRegisterDto) {
    Location parent = locationRepository.findById(locationRegisterDto.getParent_id()).orElse(null);
    Location tmp = locationRepository.findByParentAndName(parent, locationRegisterDto.getName());
    if (tmp != null) {
      throw new IllegalStateException("Location already exists");
    }

    Location location = locationRegisterDto.toLocation(parent);
    if (location == null) {
      return null;
    }
    location = locationRepository.save(location);
    return location.toLocationDto();
  }
}
