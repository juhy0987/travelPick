package com.base.demo.dto;

import java.util.UUID;

import org.springframework.data.geo.Point;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDto {
  private UUID id;
  private String name;
  private Point coordinates;
  private String timezone;
  private LocationDto parent;
}
