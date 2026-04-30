package com.lld.practice.amazonlocker;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Locker {
  private final List<Compartment> compartments;
  private final Map<String, AccessToken> accessTokenMappings;

  public Locker(List<Compartment> compartments) {
    this.compartments = compartments;
    this.accessTokenMappings = new HashMap<>();
  }

  public String depositPackage(Size size) {
    Compartment compartment = getAvailableCompartment(size);
    if (compartment == null) {
      throw new RuntimeException("No valid compartment found");
    }
    compartment.open();
    compartment.markOccupied();
    AccessToken accessToken = generateAccessToken(compartment);
    accessTokenMappings.put(accessToken.getCode(), accessToken);
    return accessToken.getCode();
  }

  public void pickup(String tokenCode) {
    if (tokenCode == null || tokenCode.isEmpty()) {
      throw new RuntimeException("Invalid token code provided");
    }

    AccessToken accessToken = accessTokenMappings.get(tokenCode);
    if (accessToken == null) {
      throw new RuntimeException("Provided access token does not exist");
    }

    if (accessToken.isExpired()) {
      throw new RuntimeException("Access token is expired");
    }

    Compartment compartment = accessToken.getCompartment();
    compartment.open();
    compartment.markFree();
    accessTokenMappings.remove(accessToken.getCode());
  }

  public void openExpiredCompartments() {

    for (AccessToken accessToken : accessTokenMappings.values()) {
      if (accessToken.isExpired()) {
        Compartment compartment = accessToken.getCompartment();
        compartment.open();
      }
    }
  }

  private Compartment getAvailableCompartment(Size packageSize) {
    for (Compartment compartment : compartments) {
      if (compartment.getSize() == packageSize && compartment.isAvailable()) {
        return compartment;
      }
    }
    return null;
  }

  private AccessToken generateAccessToken(Compartment compartment) {
    String code = UUID.randomUUID().toString();
    Instant expiration = Instant.now().plus(7, ChronoUnit.DAYS);
    return new AccessToken(code, compartment, expiration);
  }
}
