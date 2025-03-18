package com.base.demo.entity;

import java.util.List;
import java.util.UUID;

import com.base.demo.dto.AutoCompleteDto;
import com.base.demo.dto.PhotoDto;
import com.base.demo.dto.ResortDto;

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
public class Resort {
  @Id
  @GeneratedValue(strategy=GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name="location_id")
  private Location location;

  @Column(nullable=false)
  private String name;

  @Column
  private String alias;

  @Column
  private String description;

  public ResortDto toResortDto(List<PhotoDto> photos) {
    return new ResortDto(
      id,
      name,
      description,
      location.toLocationDto(),
      photos
    );
  }

  public AutoCompleteDto toAutoCompleteDto(Integer similarity, String thumbnail) {
    return new AutoCompleteDto(
      id, 
      name,
      similarity,
      thumbnail
    );
  }
}
