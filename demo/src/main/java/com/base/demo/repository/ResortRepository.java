package com.base.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.base.demo.entity.Resort;

public interface ResortRepository extends JpaRepository<Resort, Integer> {
  @Query(value="SELECT * FROM resort WHERE parent_id = :parent_id",
    nativeQuery = true)
  List<Resort> findAllByParentID(Integer parent_id);
}
