package com.base.demo.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Photo {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "resort_id")
  private Resort resort;
  
  @ManyToOne
  @JoinColumn(name = "review_id")
  private Review review;

  @Column(nullable = false)
  private byte[] data;

  @Column(nullable = false)
  private Float score;

  @Column(nullable = false)
  private String ext;

  public String getThumbnail() {
    return "data:image/" + ext + ";base64," + new String(data);
  }
}
