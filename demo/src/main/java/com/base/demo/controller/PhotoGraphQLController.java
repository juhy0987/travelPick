package com.base.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.base.demo.dto.PhotoDto;
import com.base.demo.dto.PhotoGetDto;
import com.base.demo.service.PhotoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PhotoGraphQLController {
  @Autowired
  private PhotoService photoService;

  @QueryMapping
  public PhotoDto getPhoto(@Argument UUID id) {
    return photoService.getPhoto(id);
  }

  @QueryMapping
  public List<PhotoDto> getPhotos(@Argument PhotoGetDto photoGetDto) {
    return photoService.getPhotos(photoGetDto);
  }
}
