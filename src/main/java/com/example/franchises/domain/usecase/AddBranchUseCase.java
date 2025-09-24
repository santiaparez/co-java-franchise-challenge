package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

public class AddBranchUseCase {
  private final SpringDataFranchiseRepository repo;
  public AddBranchUseCase(SpringDataFranchiseRepository repo) { this.repo = repo; }
  public Mono<Branch> execute(String franchiseId, String branchName) {
    return repo.findById(franchiseId)
      .switchIfEmpty(Mono.error(new DomainException(ErrorCodes.FRANCHISE_NOT_FOUND, "franchise.not.found")))
      .flatMap(f -> {
        Branch newBranch = new Branch(UUID.randomUUID().toString(), branchName, List.of());
        Franchise updated = new Franchise(f.id(), f.name(), concat(f.branches(), newBranch));
        return repo.save(updated).thenReturn(newBranch);
      });
  }
  private static List<Branch> concat(List<Branch> list, Branch b) { return new java.util.ArrayList<>(list) {{ add(b); }}; }
}
