package com.base.demo.entity;


import org.locationtech.jts.geom.Point;

import com.base.demo.dto.LocationDto;
import com.base.demo.dto.LocationSearchResponseDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "location")
public class Location {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="parent_id")
  private Location parent;

  @Column(nullable=false)
  private String name;

  @Column
  private String alias;

  @Column(nullable=false, columnDefinition = "POINT")
  private Point coordinates;

  @Column(nullable=false)
  private String timezone;


  public LocationDto toLocationDto() {
    return new LocationDto(
      id,
      name,
      coordinates.getY(),
      coordinates.getX(),
      timezone,
      parent != null ? parent.toLocationDto() : null
    );
  }

  public LocationSearchResponseDto toLocationSearchResponseDto(Integer similarity) {
    return new LocationSearchResponseDto(
      id,
      name,
      parent != null ? parent.toLocationDto() : null,
      coordinates.getY(),
      coordinates.getX(),
      timezone,
      similarity
    );
  }
}
