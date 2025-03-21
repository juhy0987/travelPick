package com.base.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import com.base.demo.dto.AutoCompleteDto;
import com.base.demo.dto.PhotoDto;
import com.base.demo.dto.PhotoGetDto;
import com.base.demo.dto.ResortDto;
import com.base.demo.dto.ReviewDto;
import com.base.demo.dto.ReviewRegisterDto;
import com.base.demo.dto.ReviewUpdateDto;
import com.base.demo.dto.SearchDto;
import com.base.demo.service.PhotoService;
import com.base.demo.service.ResortService;
import com.base.demo.service.ReviewService;
import com.base.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class GraphQLController {
  @Autowired
  private UserService userService;
  @Autowired
  private ResortService resortService;
  @Autowired
  private ReviewService reviewService;
  @Autowired
  private PhotoService photoService;
  
  @QueryMapping
  public String hello() {
    return "Hello, GraphQL!";
  }

  // Resort

  @QueryMapping
  public ResortDto getResort(@Argument UUID id) {
    return resortService.getResort(id);
  }

  @QueryMapping
  public List<ResortDto> searchResorts(@Argument SearchDto searchDto) {
    return resortService.searchResorts(searchDto);
  }

  @QueryMapping
  public List<AutoCompleteDto> autoComplete(@Argument String query) {
    return resortService.autoComplete(query);
  }




  // Photo

  @QueryMapping
  public PhotoDto getPhoto(@Argument UUID id) {
    return photoService.getPhoto(id);
  }

  @QueryMapping
  public List<PhotoDto> getPhotos(@Argument PhotoGetDto photoGetDto) {
    return photoService.getPhotos(photoGetDto);
  }

  // Review

  @QueryMapping
  public ReviewDto getReview(@Argument UUID id) {
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
  public String deleteReview(@Argument UUID id, @AuthenticationPrincipal UserDetails userDetails) {
    return reviewService.delete(id, userDetails);
  }
}
