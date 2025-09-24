package com.example.franchises.infrastructure.mapper;

import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.domain.model.Product;
import com.example.franchises.infrastructure.repository.documents.FranchiseItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseMapperTest {

  @Test
  void mapsItemToDomain() {
    FranchiseItem.ProductItem productItem = new FranchiseItem.ProductItem();
    productItem.setId("p-1");
    productItem.setName("Soda");
    productItem.setStock(5);

    FranchiseItem.BranchItem branchItem = new FranchiseItem.BranchItem();
    branchItem.setId("b-1");
    branchItem.setName("Downtown");
    branchItem.setProducts(List.of(productItem));

    FranchiseItem item = new FranchiseItem();
    item.setId("f-1");
    item.setName("Acme");
    item.setBranches(List.of(branchItem));

    Franchise domain = FranchiseMapper.toDomain(item);

    assertEquals("f-1", domain.id());
    assertEquals("Acme", domain.name());
    assertEquals(1, domain.branches().size());
    Branch branch = domain.branches().getFirst();
    assertEquals("Downtown", branch.name());
    Product product = branch.products().getFirst();
    assertEquals("Soda", product.name());
    assertEquals(5, product.stock());
  }

  @Test
  void mapsDomainToItem() {
    Franchise domain = new Franchise("f-1", "Acme", List.of(
        new Branch("b-1", "Downtown", List.of(new Product("p-1", "Soda", 5)))
    ));

    FranchiseItem item = FranchiseMapper.toItem(domain);

    assertEquals("f-1", item.getId());
    assertEquals("Acme", item.getName());
    assertEquals(1, item.getBranches().size());
    FranchiseItem.BranchItem branch = item.getBranches().getFirst();
    assertEquals("Downtown", branch.getName());
    FranchiseItem.ProductItem product = branch.getProducts().getFirst();
    assertEquals("Soda", product.getName());
    assertEquals(5, product.getStock());
  }

  @Test
  void mapsNullCollectionsToEmpty() {
    FranchiseItem item = new FranchiseItem();
    item.setId("f-1");
    item.setName("Acme");
    item.setBranches(null);

    Franchise domain = FranchiseMapper.toDomain(item);

    assertTrue(domain.branches().isEmpty());
  }
}

