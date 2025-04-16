package com.base.demo.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import com.base.demo.dto.ReviewDto;
import com.base.demo.dto.ReviewRegisterDto;
import com.base.demo.dto.ReviewUpdateDto;
import com.base.demo.service.ReviewService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ReviewGraphQLController {
  @Autowired
  private ReviewService reviewService;

  @QueryMapping
  public ReviewDto getReview(@Argument Integer id) {
    return reviewService.getReview(id);
  }

  @MutationMapping
  public ReviewDto createReview(@Argument ReviewRegisterDto reviewRegisterDto, @AuthenticationPrincipal UserDetails userDetails) {
    return reviewService.createReview(reviewRegisterDto, userDetails);
  }

  @MutationMapping
  public ReviewDto updateReview(@Argument ReviewUpdateDto reviewUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
    return reviewService.updateReview(reviewUpdateDto, userDetails);
  }

  @QueryMapping
  public String deleteReview(@Argument Integer id, @AuthenticationPrincipal UserDetails userDetails) {
    return reviewService.delete(id, userDetails);
  }
}
