package com.lld.practice.amazonlocker;

import java.time.Instant;

//  this is a value object
public class AccessToken {
  private final String code;
  private final Compartment compartment;
  private final Instant expiration;

  public AccessToken(String code, Compartment compartment, Instant expiration) {
    this.code = code;
    this.compartment = compartment;
    this.expiration = expiration;
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiration);
  }

  public Compartment getCompartment() {
    return compartment;
  }

  public String getCode() {
    return code;
  }
}
