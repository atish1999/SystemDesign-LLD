package com.lld.practice.vendingmachine.state;

import com.lld.practice.vendingmachine.State;
import com.lld.practice.vendingmachine.VendingMachine;

public class CoinInsertedState implements State {

  private VendingMachine vendingMachine;

  public CoinInsertedState(VendingMachine vendingMachine) {
    this.vendingMachine = vendingMachine;
  }

  @Override
  public void insert(int coins) {
    vendingMachine.setCurrentAmount(vendingMachine.getCurrentAmount() + coins);
  }

  @Override
  public void select(int aisleNumber) {}

  @Override
  public void dispense(int aisleNumber) {}
}
