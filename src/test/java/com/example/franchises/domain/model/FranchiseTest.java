package com.example.franchises.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {

  @Test
  void createFranchise_ok() {
    Franchise franchise = new Franchise("f-1", "Acme", List.of());

    assertEquals("f-1", franchise.id());
    assertEquals("Acme", franchise.name());
    assertTrue(franchise.branches().isEmpty());
  }

  @Test
  void createFranchise_invalidName() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Franchise("f-1", " \t", List.of()));

    assertEquals("invalid.franchise.name", ex.getMessage());
  }

  @Test
  void franchiseBranches_areDefensiveCopied() {
    Branch branch = new Branch("b-1", "Downtown", List.of());
    List<Branch> branches = new java.util.ArrayList<>(List.of(branch));

    Franchise franchise = new Franchise("f-1", "Acme", branches);
    branches.add(new Branch("b-2", "Uptown", List.of()));

    assertEquals(1, franchise.branches().size());
  }
}

