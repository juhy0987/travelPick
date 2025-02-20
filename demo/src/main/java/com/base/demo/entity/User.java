package com.base.demo.entity;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class User implements Serializable{
  @Id
  private String email;

  @Column(nullable=false,
          name="pass_hash")
  private String passHash;

  @Column(nullable=false)
  private String name;

  @Column(name="refresh_token")
  private String refreshToken;

  @Column
  private Date created;
}
