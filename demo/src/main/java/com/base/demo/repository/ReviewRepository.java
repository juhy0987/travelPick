package com.base.demo.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.base.demo.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
  @Query(value="SELECT * FROM review WHERE resort_id = :resort_id",
    nativeQuery = true)
  List<Review> findAllByResortID(Integer resort_id);
}
