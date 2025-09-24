package com.example.franchises.domain.model;

public record Product(String id, String name, int stock) {
  public Product {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("invalid.product.name");
    if (stock < 0) throw new IllegalArgumentException("invalid.product.stock");
  }
}
