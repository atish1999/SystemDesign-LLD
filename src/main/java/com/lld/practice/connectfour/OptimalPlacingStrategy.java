package com.lld.practice.connectfour;

import java.util.Arrays;

public class OptimalPlacingStrategy implements PlacingStrategy {

  private final int[] heights;

  public OptimalPlacingStrategy(int rows, int cols) {
    this.heights = new int[cols];
    Arrays.fill(heights, rows - 1);
  }

  @Override
  public int place(int colNo, DiscColor[][] grid) { // O(1)

    if (colNo >= grid[0].length) {
      return -1;
    }

    int rowNo = heights[colNo];
    if (rowNo < 0 || grid[rowNo][colNo] != null) {
      return -1;
    }
    --heights[colNo];
    return rowNo;
  }
}
