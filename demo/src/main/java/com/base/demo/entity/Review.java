package com.base.demo.entity;

import java.sql.Timestamp;
import java.util.List;

import com.base.demo.dto.PhotoDto;
import com.base.demo.dto.ReviewDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "review")
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "resort_id")
  private Resort resort;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column
  private String content;

  @Column(nullable=true)
  private Timestamp created;

  @Column(nullable=true,
          columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Timestamp updated;

  public ReviewDto toReviewDto(List<PhotoDto> photos) {
    return new ReviewDto(
      this.id,
      this.resort.getId(),
      this.user.getEmail(),
      this.content,
      this.created,
      this.updated,
      photos
    );
  }

  public void update(Review review) {
    boolean flag = false;
    if (review.getContent() != null) {
      this.content = review.getContent();
      flag = true;
    }
    
    if (flag) {
      this.updated = new Timestamp(System.currentTimeMillis());
    }
  }
}
