package com.lld.practice.connectfour;

public class SimplePlacingStrategy implements PlacingStrategy {

  boolean canPlace(int r, int c, DiscColor[][] grid) {
    return r >= 0 && c >= 0 && r < grid.length && c < grid[0].length && grid[r][c] == null;
  }

  @Override
  public int place(int colNo, DiscColor[][] grid) { // O(N)
    int noOfRows = grid.length;
    int rowNo = noOfRows - 1;

    while (rowNo >= 0) {
      if (canPlace(rowNo, colNo, grid)) {
        return rowNo;
      }
      --rowNo;
    }

    return -1;
  }
}
