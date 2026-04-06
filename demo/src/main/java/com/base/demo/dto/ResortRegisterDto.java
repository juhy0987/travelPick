package com.base.demo.dto;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.base.demo.entity.Location;
import com.base.demo.entity.Resort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResortRegisterDto {
  private String name;
  private Integer parent_id;
  private String description;
  private Double latitude;
  private Double longitude;
  private String timezone;

  public Resort toResort(Location ancestor) {
    return new Resort(
      null,
      ancestor,
      name,
      null,
      description
    );
  }

  public Location toLocation(Location parent) {
    GeometryFactory geometryFactory = new GeometryFactory();
    Coordinate coordinate = new Coordinate(longitude, latitude);
    Point point = geometryFactory.createPoint(coordinate);
    point.setSRID(4326); // Set SRID for compatibility with MySQL spatial data
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
