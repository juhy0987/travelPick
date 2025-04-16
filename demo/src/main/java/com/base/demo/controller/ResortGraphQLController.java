package com.base.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.base.demo.dto.AutoCompleteDto;
import com.base.demo.dto.ResortDto;
import com.base.demo.dto.SearchDto;
import com.base.demo.dto.SearchResultDto;
import com.base.demo.exception.ChromaException;
import com.base.demo.service.ResortService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ResortGraphQLController {
  @Autowired
  private ResortService resortService;
  
  @QueryMapping
  public String hello() {
    return "Hello, GraphQL!";
  }

  // Resort

  @QueryMapping
  public ResortDto getResort(@Argument Integer id) {
    return resortService.getResort(id);
  }

  @QueryMapping
  public List<SearchResultDto> searchResorts(@Argument SearchDto searchDto) throws ChromaException {
    return resortService.searchResorts(searchDto);
  }

  @QueryMapping
  public List<AutoCompleteDto> autoComplete(@Argument String query) {
    return resortService.autoComplete(query);
  }

  @MutationMapping
  public ResortDto createResort(@Argument ResortDto resortDto) {
    return resortService.createResort(resortDto);
  }
}
