package com.lld.practice.vendingmachine;

import java.util.UUID;

public class Product {
  private final String id;
  private final String name;
  private final int price;

  public Product(String name, int price) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.price = price;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getPrice() {
    return price;
  }
}
