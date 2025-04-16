package com.base.demo.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.base.demo.dto.PhotoDto;
import com.base.demo.dto.PhotoGetDto;
import com.base.demo.entity.Photo;
import com.base.demo.repository.PhotoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PhotoService {
  @Autowired
  private PhotoRepository photoRepository;
  
  @Value("${app.default.image.num:10}")
  private int defaultImageNum;

  public PhotoDto getPhoto(Integer id) {
    Photo target = photoRepository.findById(id).orElse(null);
    if (target == null) {
      return null;
    }

    return target.toPhotoDto(0);
  }

  public List<PhotoDto> getPhotos(PhotoGetDto photoGetDto) {
    System.out.println(photoGetDto.getResort_id());
    Photo tmp = photoRepository.findById(0).orElse(null);
    if (tmp == null)
      return null;

    List<Photo> photos = photoRepository.findAllByResortID(photoGetDto.getResort_id());
    System.out.println(photos.toString());
    AtomicInteger index = new AtomicInteger(photoGetDto.getOffset());

    int limit = (photoGetDto.getLimit() != null) ? photoGetDto.getLimit() : defaultImageNum;

    return photos.stream()
      .skip(photoGetDto.getOffset())
      .limit(limit)
      .map(photo -> photo.toPhotoDto(index.getAndIncrement()))
      .collect(Collectors.toList());
  }
}
