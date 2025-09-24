package com.example.franchises.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

  @Test
  void createProduct_ok() {
    Product product = new Product("p-1", "Soda", 10);

    assertEquals("p-1", product.id());
    assertEquals("Soda", product.name());
    assertEquals(10, product.stock());
  }

  @Test
  void createProduct_invalidName() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Product("p-1", "  ", 10));

    assertEquals("invalid.product.name", ex.getMessage());
  }

  @Test
  void createProduct_invalidStock() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Product("p-1", "Soda", -1));

    assertEquals("invalid.product.stock", ex.getMessage());
  }
}

