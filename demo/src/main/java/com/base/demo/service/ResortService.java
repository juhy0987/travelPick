package com.base.demo.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.FuzzyScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.base.demo.dto.AutoCompleteDto;
import com.base.demo.entity.Photo;
import com.base.demo.entity.Resort;
import com.base.demo.repository.LocationRepository;
import com.base.demo.repository.PhotoRepository;
import com.base.demo.repository.ResortRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResortService {
  @Autowired
  private LocationRepository locationRepository;
  @Autowired
  private ResortRepository resortRepository;
  @Autowired
  private PhotoRepository photoRepository;

  private final FuzzyScore fuzzyScore = new FuzzyScore(java.util.Locale.KOREAN);
  private final Random random = new Random();

  public Resort getResort(UUID id) {
    Resort resort = resortRepository.findById(id).orElse(null);
    if (resort == null) {
      return null;
    }

    return resort;
  }

  public List<AutoCompleteDto> autoComplete(String query) {
    List<Resort> resorts = resortRepository.findAll();
    return resorts.stream()
      .map(resort -> {
        List<Photo> tmp = photoRepository.findByLocationID(resort.getId());
        return new AutoCompleteDto(
          resort.getId().toString(),
          resort.getName(),
          fuzzyScore.fuzzyScore(resort.getName(), query),
          tmp.isEmpty() ? "" : tmp.get(random.nextInt(tmp.size())).getThumbnail()
        );
      })
      .filter(dto -> dto.getSimilarity() > 0)
      .sorted((r1, r2) -> Integer.compare(r1.getSimilarity(), r2.getSimilarity()))
      .collect(Collectors.toList());
  }
}
