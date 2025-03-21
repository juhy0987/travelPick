package com.base.demo.dto;

import java.util.List;
import java.util.UUID;

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
public class ReviewUpdateDto {
  private UUID id;
  private String content;
  private List<UUID> delete_photos;
  private List<String> add_photos;

  public Review toReview(Resort resort, User user) {
    return new Review(
      this.id,
      resort,
      user,
      this.content,
      null,
      null,
      null
    );
  }
}
