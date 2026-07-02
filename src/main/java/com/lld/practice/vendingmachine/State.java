package com.lld.practice.vendingmachine;

public interface State {
  void insert(int coins);

  void select(int aisleNumber);

  void dispense(int aisleNumber);
}
