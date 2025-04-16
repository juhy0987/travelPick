package com.base.demo.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.FuzzyScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.base.demo.dto.AutoCompleteDto;
import com.base.demo.dto.PhotoDto;
import com.base.demo.dto.ResortDto;
import com.base.demo.dto.SearchDto;
import com.base.demo.dto.SearchResultDto;
import com.base.demo.entity.Photo;
import com.base.demo.entity.Resort;
import com.base.demo.exception.ChromaException;
import com.base.demo.repository.ChromaRepository;
import com.base.demo.repository.LocationRepository;
import com.base.demo.repository.PhotoRepository;
import com.base.demo.repository.ResortRepository;
import com.base.demo.utils.ImageSize;

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
  @Autowired
  private ChromaRepository chromaRepository;

  @Value("${app.similarity.minimum}")
  private int minimumSimilarity;

  @Value("${app.default.thumbnail.num}")
  private int defaultThumbnailNum;
 
  private final FuzzyScore fuzzyScore = new FuzzyScore(java.util.Locale.KOREAN);
  private final Random random = new Random();

  public ResortDto getResort(Integer id) {
    Resort resort = resortRepository.findById(id).orElse(null);
    if (resort == null) {
      return null;
    }

    List<Photo> photos = photoRepository.findAllByResortID(id);
    AtomicInteger index = new AtomicInteger(0);
    Collections.shuffle(photos);

    List<PhotoDto> photoDtos = photos.stream()
      .limit(defaultThumbnailNum)
      .map(photo -> photo.toPhotoDto(index.getAndIncrement(), ImageSize.THUMBNAIL))
      .collect(Collectors.toList());

    return resort.toResortDto(photoDtos);
  }

  public List<AutoCompleteDto> autoComplete(String query) {
    List<Resort> resorts = resortRepository.findAll();
    return resorts.stream()
      .map(resort -> {
        List<Photo> tmp = photoRepository.findAllByResortID(resort.getId());
        return resort.toAutoCompleteDto(
          fuzzyScore.fuzzyScore(resort.getName(), query),
          tmp.isEmpty() ? "" : tmp.get(random.nextInt(tmp.size())).getDataURL()
        );
      })
      .filter(dto -> dto.getSimilarity() > minimumSimilarity)
      .sorted((r1, r2) -> Integer.compare(r1.getSimilarity(), r2.getSimilarity()))
      .collect(Collectors.toList());
  }

  public List<SearchResultDto> searchResorts(SearchDto searchDto) throws ChromaException {
    try {
      List<Map<String, String>> results = chromaRepository.search(searchDto);
      return results.stream()
      .map(result -> {
        int id = Integer.parseUnsignedInt(result.get("id"));
        Resort resort = resortRepository.findById(id).orElse(null);
        if (resort == null)
          return null;
        
        AtomicInteger index = new AtomicInteger(0);
        List<PhotoDto> photoDtos = photoRepository.findRandomByResortID(id, PageRequest.of(0, defaultThumbnailNum))
          .stream()
          .map(photo -> photo.toPhotoDto(index.getAndIncrement(), ImageSize.THUMBNAIL))
          .collect(Collectors.toList());
        return resort.toSearchResultDto(photoDtos, Double.parseDouble(result.get("score")));
      })
      .collect(Collectors.toList());
    } catch (Exception e) {
      throw new ChromaException("Chroma server error", e);
    }
    

    
  }

  public ResortDto createResort(ResortDto resortDto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createResort'");
  }

  
}
