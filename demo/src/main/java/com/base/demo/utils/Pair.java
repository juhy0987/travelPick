package com.base.demo.utils;

public class Pair<K, V> {
  private final K first;
  private final V second;
  
  public Pair(K first, V second) {
    this.first = first;
    this.second = second;
  }

  public K getFirst() {
    return first;
  }
  public V getSecond() {
    return second;
  }

  @Override
  public String toString() {
    return "Pair{" +
      "first=" + first +
      ", second=" + second +
      '}';
  }
}
