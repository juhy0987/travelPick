package com.base.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor 
@Getter
@Setter
@ToString
public class AutoCompleteDto {
  private String id;
  private String name;
  private Integer similarity;
  private String thumbnail;
}
