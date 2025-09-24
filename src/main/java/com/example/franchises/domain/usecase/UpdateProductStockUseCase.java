package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.*;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Mono;
import java.util.*;

public class UpdateProductStockUseCase {
  private final SpringDataFranchiseRepository repo;
  public UpdateProductStockUseCase(SpringDataFranchiseRepository repo) { this.repo = repo; }
  public Mono<Product> execute(String franchiseId, String branchId, String productId, int stock){
    return repo.findById(franchiseId)
      .switchIfEmpty(Mono.error(new DomainException(ErrorCodes.FRANCHISE_NOT_FOUND, "franchise.not.found")))
      .flatMap(f -> {
        Branch branch = f.branches().stream().filter(b -> b.id().equals(branchId)).findFirst()
          .orElseThrow(() -> new DomainException(ErrorCodes.BRANCH_NOT_FOUND, "branch.not.found"));
        Product product = branch.products().stream().filter(p -> p.id().equals(productId)).findFirst()
          .orElseThrow(() -> new DomainException(ErrorCodes.PRODUCT_NOT_FOUND, "product.not.found"));
        Product updatedProduct = new Product(product.id(), product.name(), stock);
        Branch updatedBranch = new Branch(branch.id(), branch.name(), replaceProduct(branch.products(), updatedProduct));
        Franchise updated = new Franchise(f.id(), f.name(), replaceBranch(f.branches(), updatedBranch));
        return repo.save(updated).thenReturn(updatedProduct);
      });
  }
  private static java.util.List<Product> replaceProduct(java.util.List<Product> list, Product updated){
    java.util.List<Product> res = new java.util.ArrayList<>(list);
    for (int i=0;i<res.size();i++) if (res.get(i).id().equals(updated.id())) { res.set(i, updated); break; }
    return res;
  }
  private static java.util.List<Branch> replaceBranch(java.util.List<Branch> list, Branch updated){
    java.util.List<Branch> res = new java.util.ArrayList<>(list);
    for (int i=0;i<res.size();i++) if (res.get(i).id().equals(updated.id())) { res.set(i, updated); break; }
    return res;
  }
}
