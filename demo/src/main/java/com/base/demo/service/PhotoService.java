package com.base.demo.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.base.demo.dto.PhotoDto;
import com.base.demo.dto.PhotoGetDto;
import com.base.demo.entity.Photo;
import com.base.demo.repository.PhotoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PhotoService {
  @Autowired
  private PhotoRepository photoRepository;

  public PhotoDto getPhoto(UUID id) {
    Photo target = photoRepository.findById(id).orElse(null);
    if (target == null) {
      return null;
    }
    return target.toPhotoDto(0);
  }

  public List<PhotoDto> getPhotos(PhotoGetDto photoGetDto) {
    List<Photo> photos = photoRepository.findAllByResortID(photoGetDto.getResort_id());
    AtomicInteger index = new AtomicInteger(photoGetDto.getOffset());
    return photos.stream()
      .skip(photoGetDto.getOffset())
      .limit(photoGetDto.getLimit())
      .map(photo -> photo.toPhotoDto(index.getAndIncrement()))
      .collect(Collectors.toList());
  }
}
