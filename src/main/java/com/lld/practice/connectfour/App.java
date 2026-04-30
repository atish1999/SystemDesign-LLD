package com.lld.practice.connectfour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Hello world! */
public class App {
  public static void main(String[] args) throws IOException {
    Player atish = new Player("Atish", DiscColor.YELLOW);
    Player atri = new Player("Atri", DiscColor.RED);
    Game game = new Game(atish, atri);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    outer:
    while (true) {
      System.out.printf("%s's turn. Enter the colNo%n", game.currentPlayer().getName());
      String input = reader.readLine();
      int colNo = Integer.parseInt(input);
      boolean isAbleToMakeMove = game.makeMove(colNo);
      if (!isAbleToMakeMove) {
        System.out.println("Please Check");
      }

      switch (game.currentState()) {
        case WON -> {
          System.out.printf("Winner is: %s%n", game.winner().getName());
          break outer;
        }
        case DRAW -> {
          System.out.println("Game is Drawn");
          break outer;
        }
      }
    }
  }
}
