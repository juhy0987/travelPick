package com.base.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.base.demo.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
  @Query(value="SELECT * FROM photo WHERE location_id = :location_id",
    nativeQuery = true)
  Photo findByLocationID(UUID location_id);
}
