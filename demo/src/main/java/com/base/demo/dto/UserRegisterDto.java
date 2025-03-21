package com.base.demo.dto;

import java.sql.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.base.demo.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserRegisterDto {
  private String email;
  private String password;
  private String name;

  public User toUser(BCryptPasswordEncoder passwordEncoder) {
    return new User(
      this.email,
      passwordEncoder.encode(this.password),
      this.name,
      null,
      new Date(System.currentTimeMillis()));
  }
}
