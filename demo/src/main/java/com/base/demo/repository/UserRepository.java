package com.base.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.base.demo.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

  @Query(value="SELECT * FROM user WHERE email = :email",
    nativeQuery = true)
  User findByEmail(String email);
}
