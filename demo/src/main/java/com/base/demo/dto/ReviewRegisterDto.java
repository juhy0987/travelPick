package com.base.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.base.demo.entity.Resort;
import com.base.demo.entity.Review;
import com.base.demo.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewRegisterDto {
  private Integer resort_id;
  private String content;
  private List<String> photos;

  public Review toReview(Resort resort, User user) {
    return new Review(
      null,
      resort,
      user,
      this.content,
      LocalDateTime.now(),
      null
    );
  }
}
