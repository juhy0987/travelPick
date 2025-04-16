package com.base.demo.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchDto {
  private String query;
  private List<String> dataurls;
  private int count;

  public Map<String, String> toMap() {
    return Map.of(
      "query", query,
      "dataurls", String.join(",", dataurls),
      "count", String.valueOf(count)
    );
  }
}
