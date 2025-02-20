package com.base.demo.dto;

import java.sql.Date;

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
public class UserDto {
  private String email;
  private String passHash;
  private String name;
  private String refreshToken;
  private Date created;

  public User toEntity() {
    return new User(
      this.email,
      this.passHash,
      this.name, 
      this.refreshToken, 
      this.created);
  }
}
