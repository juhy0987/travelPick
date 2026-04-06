package com.base.demo.dto;

import java.time.LocalDateTime;
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
  private UserViewDto user;
  private String content;
  private LocalDateTime created;
  private LocalDateTime updated;
  private List<PhotoDto> photos;
}
