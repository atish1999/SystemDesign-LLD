package com.lld.practice.vendingmachine;

public class VendingMachine {
  private Inventory inventory;
  private State currentState;
  private int currentAmount;

  public VendingMachine() {}

  public void insertCoints(int coins) {
    currentState.insert(coins);
  }

  public void selectProduct(int aisleNumber) {
    currentState.select(aisleNumber);
  }

  public void dispense(int aisleNumber) {
    currentState.dispense(aisleNumber);
  }

  public void addProduct(Product product) {}

  public int getCurrentAmount() {
    return this.currentAmount;
  }

  public void setCurrentAmount(int amount) {
    this.currentAmount = amount;
  }
}
