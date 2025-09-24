package com.example.franchises.infrastructure.mapper;

import com.example.franchises.domain.model.*;
import com.example.franchises.infrastructure.repository.documents.FranchiseItem;


import java.util.List;

public class FranchiseMapper {
  public static Franchise toDomain(FranchiseItem item){
    return new Franchise(
            item.getId(), item.getName(),
            item.getBranches()==null? List.of() : item.getBranches().stream()
                    .map(b -> new Branch(b.getId(), b.getName(), b.getProducts()==null? List.of() : b.getProducts().stream()
                            .map(p -> new Product(p.getId(), p.getName(), p.getStock())).toList()))
                    .toList()
    );
  }
  public static FranchiseItem toItem(Franchise domain){
    var item = new FranchiseItem();
    item.setId(domain.id()); item.setName(domain.name());
    item.setBranches(domain.branches().stream().map(b -> {
      var bi = new FranchiseItem.BranchItem(); bi.setId(b.id()); bi.setName(b.name());
      bi.setProducts(b.products().stream().map(p -> { var pi = new FranchiseItem.ProductItem(); pi.setId(p.id()); pi.setName(p.name()); pi.setStock(p.stock()); return pi; }).toList());
      return bi;
    }).toList());
    return item;
  }
}

