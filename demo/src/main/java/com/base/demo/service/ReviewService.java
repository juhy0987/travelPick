package com.base.demo.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.base.demo.dto.ReviewDto;
import com.base.demo.dto.ReviewRegisterDto;
import com.base.demo.dto.ReviewUpdateDto;
import com.base.demo.entity.Photo;
import com.base.demo.entity.Resort;
import com.base.demo.entity.Review;
import com.base.demo.entity.User;
import com.base.demo.repository.PhotoRepository;
import com.base.demo.repository.ResortRepository;
import com.base.demo.repository.ReviewRepository;
import com.base.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ResortRepository resortRepository;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private PhotoRepository photoRepository;



  public ReviewDto getReview(UUID id) {
    Review review = reviewRepository.findById(id).orElse(null);
    if (review == null) {
      return null;
    }

    AtomicInteger index = new AtomicInteger(0);

    return review.toReviewDto(
      photoRepository.findAllByReviewID(id)
        .stream()
        .map(photo -> photo.toPhotoDto(index.getAndIncrement()))
        .toList()
    );
  }

  public ReviewDto createReview(ReviewRegisterDto reviewRegisterDto, UserDetails userDetails) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    if (user == null) {
      throw new AuthenticationException("User not found") {};
    }

    Resort resort = resortRepository.findById(reviewRegisterDto.getResort_id()).orElse(null);
    if (resort == null) {
      throw new IllegalArgumentException("Resort not found");
    }

    AtomicInteger index = new AtomicInteger(0);
    
    Review review = reviewRepository.save(reviewRegisterDto.toReview(resort, user));
    List<Photo> photos = reviewRegisterDto.getPhotos()
      .stream()
      .map(dataurl -> {
        return new Photo(resort, review, dataurl);
      })
      .toList();
    photoRepository.saveAll(photos);
    return review.toReviewDto(
      photos.stream()
        .map(photo -> photo.toPhotoDto(index.getAndIncrement()))
        .toList()
    );
  }

  public ReviewDto updateReview(ReviewUpdateDto reviewUpdateDto, UserDetails userDetails) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    if (user == null) {
      throw new AuthenticationException("User not found") {};
    }

    Review review = reviewRepository.findById(reviewUpdateDto.getId()).orElse(null);
    if (review == null) {
      throw new IllegalArgumentException("Review not found");
    }
    else if (!review.getUser().equals(user)) {
      throw new AuthenticationException("User not authorized") {};
    }

    Resort resort = review.getResort();

    review.update(reviewUpdateDto.toReview(resort, user));
    Review target = reviewRepository.save(review);

    AtomicInteger index = new AtomicInteger(0);
    List<Photo> photos = reviewUpdateDto.getAdd_photos()
      .stream()
      .map(dataurl -> {
        return new Photo(resort, target, dataurl);
      })
      .toList();
    photoRepository.saveAll(photos);

    reviewUpdateDto.getDelete_photos()
      .forEach(photo_id -> {
        photoRepository.deleteById(photo_id);
      });

    return target.toReviewDto(
      photoRepository.findAllByReviewID(target.getId())
        .stream()
        .map(photo -> photo.toPhotoDto(index.getAndIncrement()))
        .toList()
    );
  }

  public String delete(UUID id, UserDetails userDetails) {
    User user = userRepository.findByEmail(userDetails.getUsername());
    if (user == null) {
      throw new AuthenticationException("User not found") {};
    }

    Review review = reviewRepository.findById(id).orElse(null);
    if (review == null) {
      throw new IllegalArgumentException("Review not found");
    }
    else if (!review.getUser().equals(user)) {
      throw new AuthenticationException("User not authorized") {};
    }
    
    reviewRepository.delete(review);
    return "Review deleted";
  }
}
