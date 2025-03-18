package com.base.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.base.demo.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
  @Query(value="SELECT * FROM photo WHERE resort_id = :resort_id",
    nativeQuery = true)
  List<Photo> findAllByResortID(UUID resort_id);

  @Query(value="SELECT * FROM photo WHERE review_id = :review_id",
    nativeQuery = true)
  List<Photo> findAllByReviewID(UUID review_id);
}
