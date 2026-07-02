package com.lld.practice.vendingmachine;

import java.util.Map;

public class Inventory {

  private Map<Integer, Product> productsBySlotId;
  private Map<String, Integer> quantityMapByProductId;
  private int totalCapacity;

  public void add(Product product) {}

  public void remove(Product product) {}

  public Product getProductAt(int aisleNumber) {
    return null;
  }
}
