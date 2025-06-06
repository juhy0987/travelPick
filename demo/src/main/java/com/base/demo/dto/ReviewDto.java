package com.base.demo.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewDto {
  private Integer id;
  private Integer resort_id;
  private String user_id;
  private String content;
  private Date created;
  private Date updated;
  private List<PhotoDto> photos;
}
