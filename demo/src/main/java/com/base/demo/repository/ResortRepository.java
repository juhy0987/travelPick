package com.base.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.base.demo.entity.Resort;

public interface ResortRepository extends JpaRepository<Resort, UUID> {
  
}
