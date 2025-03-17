package com.base.demo.entity;

import java.util.UUID;

import org.springframework.data.geo.Point;

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
public class Location {
  @Id
  @GeneratedValue(strategy=GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name="parent_id")
  private Location parent;

  @Column(nullable=false)
  private String name;

  @Column
  private String alias;

  @Column(nullable=false)
  private Point coordinates;

  @Column(nullable=false)
  private Integer timezone;
}
