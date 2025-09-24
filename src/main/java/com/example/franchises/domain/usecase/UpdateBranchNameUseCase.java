package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Mono;

public class UpdateBranchNameUseCase {
  private final SpringDataFranchiseRepository repo;
  public UpdateBranchNameUseCase(SpringDataFranchiseRepository repo) { this.repo = repo; }
  public Mono<Branch> execute(String franchiseId, String branchId, String name){
    return repo.findById(franchiseId)
      .switchIfEmpty(Mono.error(new DomainException(ErrorCodes.FRANCHISE_NOT_FOUND, "franchise.not.found")))
      .flatMap(f -> {
        Branch branch = f.branches().stream().filter(b -> b.id().equals(branchId)).findFirst()
          .orElseThrow(() -> new DomainException(ErrorCodes.BRANCH_NOT_FOUND, "branch.not.found"));
        Branch updatedBranch = new Branch(branch.id(), name, branch.products());
        Franchise updated = new Franchise(f.id(), f.name(), replaceBranch(f.branches(), updatedBranch));
        return repo.save(updated).thenReturn(updatedBranch);
      });
  }
  private static java.util.List<Branch> replaceBranch(java.util.List<Branch> list, Branch updated){
    java.util.List<Branch> res = new java.util.ArrayList<>(list);
    for (int i=0;i<res.size();i++) if (res.get(i).id().equals(updated.id())) { res.set(i, updated); break; }
    return res;
  }
}
