package com.base.demo.repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.base.demo.dto.SearchDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ChromaRepository{
  @Value("${chroma.server.host:http://localhost}")
  private String PYTHON_SERVER_HOST;
  @Value("${chroma.server.port:50000}")
  private String PYTHON_SERVER_PORT;

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public ChromaRepository() {
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public void store(List<String> objs, List<Map<String, String>> metadata, String type) throws Exception{
    String url = PYTHON_SERVER_HOST + PYTHON_SERVER_PORT + "/api/store";
    String requestBodyJson = objectMapper.writeValueAsString(Map.of("texts", objs, "metadata", metadata));

    HttpRequest request = HttpRequest.newBuilder()
      .uri(new URI(url))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
      .build();
    
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) {
      throw new RuntimeException("Failed to store data: " + response.body());
    }
  }

  public List<Map<String, String>> search(SearchDto searchDto) throws Exception{
    String url = PYTHON_SERVER_HOST + PYTHON_SERVER_PORT + "/api/search";
    String requestBodyJson = objectMapper.writeValueAsString(searchDto.toMap());

    HttpRequest request = HttpRequest.newBuilder()
      .uri(new URI(url))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
      .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) {
      throw new RuntimeException("Failed to search data: " + response.body());
    }

    List<Map<String, String>> result = objectMapper.readValue(response.body(), List.class);
    return result;
  }
}
