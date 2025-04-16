package com.base.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.base.demo.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {

  @Query(value="SELECT * FROM photo WHERE resort_id = :resort_id",
    nativeQuery = true)
  List<Photo> findAllByResortID(Integer resort_id);

  @Query(value="SELECT * FROM photo WHERE review_id = :review_id",
    nativeQuery = true)
  List<Photo> findAllByReviewID(Integer review_id);

  @Query(value="SELECT * FROM photo WHERE resort_id = :resort_id ORDER BY RAND()", 
    nativeQuery = true)
  List<Photo> findRandomByResortID(Integer resort_id, Pageable pageable);

  @Query(value="SELECT * FROM photo WHERE review_id = :review_id ORDER BY RAND()", 
    nativeQuery = true)
  List<Photo> findRandomByReviewID(Integer review_id, Pageable pageable);
}
