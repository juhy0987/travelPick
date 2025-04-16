package com.base.demo.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDto {
  private Integer id;
  private String name;
  private Double latitude;
  private Double longitude;
  private String timezone;
  private LocationDto parent;
}
