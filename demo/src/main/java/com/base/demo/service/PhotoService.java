package com.base.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.base.demo.repository.PhotoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PhotoService {
  @Autowired
  private PhotoRepository photoRepository;
}
