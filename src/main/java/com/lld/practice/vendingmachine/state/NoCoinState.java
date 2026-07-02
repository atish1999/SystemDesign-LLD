package com.lld.practice.vendingmachine.state;

import com.lld.practice.vendingmachine.State;
import com.lld.practice.vendingmachine.VendingMachine;

public class NoCoinState implements State {

  private VendingMachine vendingMachine;

  public NoCoinState(VendingMachine vendingmachine) {
    this.vendingMachine = vendingmachine;
  }

  @Override
  public void insert(int coins) {
    vendingMachine.setCurrentAmount(coins);
  }

  @Override
  public void select(int aisleNumber) {
    throw new IllegalStateException("");
  }

  @Override
  public void dispense(int aisleNumber) {}
}
