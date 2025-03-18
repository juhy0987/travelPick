package com.base.demo.utils;


public record ImageSize(int width, int height) {
  public static final ImageSize FULL = new ImageSize(960, 540);
  public static final ImageSize THUMBNAIL = new ImageSize(100, 100);
}
