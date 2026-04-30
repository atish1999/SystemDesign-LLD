package com.lld.practice.connectfour;

public class Game {
  private final Player player1;
  private final Player player2;
  private final Board board;
  private Player winner;
  private Player currentPlayer;
  private GameState state;

  public Game(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
    this.currentPlayer = player1;
    this.board = new Board();
    state = GameState.IN_PROGRESS;
  }

  public boolean makeMove(int col) {
    if (state != GameState.IN_PROGRESS) {
      return false;
    }
    int row = board.place(currentPlayer.getDiscColor(), col);
    board.printState();
    if (row == -1) {
      System.out.printf("%s can not place in the col %d%n", currentPlayer.getDiscColor(), col);
      return false;
    }
    // check if anybody wins
    if (board.checkWin(row, col, currentPlayer.getDiscColor())) {
      state = GameState.WON;
      winner = currentPlayer;
    }
    // check if there is a draw
    if (board.isFull()) {
      state = GameState.DRAW;
    }
    // update the nextPlayer
    updateNextPlayer();
    return true;
  }

  private void updateNextPlayer() {
    currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
  }

  public GameState currentState() {
    return state;
  }

  public Player winner() {
    return winner;
  }

  public Player currentPlayer() {
    return currentPlayer;
  }
}
