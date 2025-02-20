package com.base.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.base.demo.entity.Location;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
