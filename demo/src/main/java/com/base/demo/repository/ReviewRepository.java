package com.base.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.base.demo.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  
}
