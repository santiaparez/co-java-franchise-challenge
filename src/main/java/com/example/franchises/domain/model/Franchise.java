package com.example.franchises.domain.model;

import java.util.List;

public record Franchise(String id, String name, List<Branch> branches) {
  public Franchise {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("invalid.franchise.name");
    branches = branches == null ? List.of() : List.copyOf(branches);
  }
}
