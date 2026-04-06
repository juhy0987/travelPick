package com.base.demo.dto;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.base.demo.entity.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationRegisterDto {
  private String name;
  private Integer parent_id;
  private Double latitude;
  private Double longitude;
  private String timezone;

  public Location toLocation(Location parent) {
    GeometryFactory geometryFactory = new GeometryFactory();
    Coordinate coordinate = new Coordinate(longitude, latitude);
    Point point = geometryFactory.createPoint(coordinate);
    return new Location(
      null,
      parent,
      name,
      null,
      point,
      timezone
    );
  }
}
