package com.base.demo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class SearchDto {
  private String query = "";
  private List<String> dataurls = new ArrayList<>();
  private int count = 10;

  public Map<String, String> toMap() {
    return Map.of(
      "query", query,
      "dataurls", String.join(",", dataurls),
      "count", String.valueOf(count)
    );
  }
}
