package com.example.franchises.domain.model;

import java.util.List;

public record Branch(String id, String name, List<Product> products) {
  public Branch {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("invalid.branch.name");
    products = products == null ? List.of() : List.copyOf(products);
  }
}
