package com.example.franchises.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

  @Test
  void createBranch_ok() {
    Branch branch = new Branch("b-1", "Downtown", List.of(new Product("p-1", "Soda", 5)));

    assertEquals("b-1", branch.id());
    assertEquals("Downtown", branch.name());
    assertEquals(1, branch.products().size());
  }

  @Test
  void createBranch_invalidName() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Branch("b-1", "", List.of()));

    assertEquals("invalid.branch.name", ex.getMessage());
  }

  @Test
  void branchProducts_areDefensiveCopied() {
    Product product = new Product("p-1", "Soda", 5);
    List<Product> products = new java.util.ArrayList<>(List.of(product));

    Branch branch = new Branch("b-1", "Downtown", products);
    products.add(new Product("p-2", "Juice", 3));

    assertEquals(1, branch.products().size());
  }
}

