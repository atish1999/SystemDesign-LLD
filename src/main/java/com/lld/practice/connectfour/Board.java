package com.lld.practice.connectfour;

public class Board {
  private final int rows;
  private final int cols;
  private final DiscColor[][] grid;
  private final PlacingStrategy placingStrategy;

  public Board(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.grid = new DiscColor[rows][cols];
    this.placingStrategy = new OptimalPlacingStrategy(rows, cols);
  }

  public Board() {
    this(7, 6);
  }

  public int place(DiscColor discColor, int colNo) {
    int rowNo = this.placingStrategy.place(colNo, grid);
    if (rowNo != -1) {
      grid[rowNo][colNo] = discColor;
    }
    return rowNo;
  }

  public boolean checkWin(int rowNo, int colNo, DiscColor discColor) {
    int[][] directions =
        new int[][] {
          {
            0, 1 // Vertical
          },
          {
            1, 0 // Horizontal
          },
          {
            1, 1 // Right Upper Diagonal
          },
          {
            1, -1 // Right lower Diagonal
          }
        };

    for (int[] direction : directions) {
      int row = direction[0];
      int col = direction[1];
      int count = 1;
      count += countInDirection(rowNo, colNo, row, col, discColor);
      count += countInDirection(rowNo, colNo, -row, -col, discColor);
      if (count >= 4) {
        return true;
      }
    }

    return false;
  }

  boolean isBound(int r, int c) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
  }

  private int countInDirection(int rowNo, int colNo, int row, int col, DiscColor discColor) {

    rowNo += row;
    colNo += col;

    int step = 0;
    while (isBound(rowNo, colNo) && grid[rowNo][colNo] == discColor) {
      rowNo += row;
      colNo += col;
      ++step;
    }

    return step;
  }

  public boolean isFull() {

    for (int rowNo = 0; rowNo < rows; ++rowNo) {
      for (int colNo = 0; colNo < cols; ++colNo) {
        if (grid[rowNo][colNo] == null) {
          return false;
        }
      }
    }
    return true;
  }

  public void printState() {
    for (DiscColor[] r : grid) {
      for (DiscColor c : r) {
        if (c == null) {
          System.out.print("___");
        } else {
          System.out.print(c == DiscColor.YELLOW ? "Y" : "R");
        }
        System.out.print("  ");
      }
      System.out.println();
    }
  }
}
