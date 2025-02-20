package com.base.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.base.demo.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
  
}
