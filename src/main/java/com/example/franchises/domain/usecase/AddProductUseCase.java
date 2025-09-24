package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.*;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Mono;
import java.util.*;

public class AddProductUseCase {
  private final SpringDataFranchiseRepository repo;
  public AddProductUseCase(SpringDataFranchiseRepository repo) { this.repo = repo; }
  public Mono<Product> execute(String franchiseId, String branchId, String name, int stock) {
    return repo.findById(franchiseId)
      .switchIfEmpty(Mono.error(new DomainException(ErrorCodes.FRANCHISE_NOT_FOUND, "franchise.not.found")))
      .flatMap(f -> {
        Branch branch = f.branches().stream().filter(b -> b.id().equals(branchId)).findFirst()
          .orElseThrow(() -> new DomainException(ErrorCodes.BRANCH_NOT_FOUND, "branch.not.found"));
        Product newProduct = new Product(UUID.randomUUID().toString(), name, stock);
        Branch updatedBranch = new Branch(branch.id(), branch.name(), concat(branch.products(), newProduct));
        Franchise updated = new Franchise(f.id(), f.name(), replaceBranch(f.branches(), updatedBranch));
        return repo.save(updated).thenReturn(newProduct);
      });
  }
  private static java.util.List<Product> concat(java.util.List<Product> list, Product p){ return new java.util.ArrayList<>(list) {{ add(p); }}; }
  private static java.util.List<Branch> replaceBranch(java.util.List<Branch> list, Branch updated){
    java.util.List<Branch> res = new java.util.ArrayList<>(list);
    for (int i=0;i<res.size();i++) if (res.get(i).id().equals(updated.id())) { res.set(i, updated); break; }
    return res;
  }
}
