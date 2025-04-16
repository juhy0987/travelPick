package com.base.demo.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhotoGetDto {
  private Integer resort_id;
  private Integer offset;
  private Integer limit;
}
