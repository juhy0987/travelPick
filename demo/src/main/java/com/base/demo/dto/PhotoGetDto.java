package com.base.demo.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhotoGetDto {
  private UUID resort_id;
  private Integer offset;
  private Integer limit;
}
