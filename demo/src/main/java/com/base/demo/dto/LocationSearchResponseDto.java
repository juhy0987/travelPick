package com.base.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocationSearchResponseDto {
  private Integer id;
  private String name;
  private LocationDto parent;
  private Double latitude;
  private Double longitude;
  private String timezone;
  private Integer similarity;
}
