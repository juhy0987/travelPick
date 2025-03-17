package com.base.demo.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResortDto {
  private UUID id;
  private String name;
  private String description;
  private List<LocationDto> ancestors;
  private List<String> photos;
}
