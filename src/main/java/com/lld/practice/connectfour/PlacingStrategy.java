package com.lld.practice.connectfour;

public interface PlacingStrategy {
  int place(int colNo, DiscColor[][] grid);
}
