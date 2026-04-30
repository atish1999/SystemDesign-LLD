package com.lld.practice.connectfour;

import java.util.Objects;

public class Player {
  String name;
  DiscColor discColor;

  public Player(String name, DiscColor discColor) {
    this.name = name;
    this.discColor = discColor;
  }

  public String getName() {
    return name;
  }

  public DiscColor getDiscColor() {
    return discColor;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return Objects.equals(name, player.name) && discColor == player.discColor;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, discColor);
  }
}
