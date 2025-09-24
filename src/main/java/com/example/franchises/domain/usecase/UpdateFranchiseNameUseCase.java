package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.*;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Mono;
import java.util.*;

public class UpdateFranchiseNameUseCase {
  private final SpringDataFranchiseRepository repo;
  public UpdateFranchiseNameUseCase(SpringDataFranchiseRepository repo) { this.repo = repo; }
  public Mono<Franchise> execute(String franchiseId, String name){
    return repo.findById(franchiseId)
      .switchIfEmpty(Mono.error(new DomainException(ErrorCodes.FRANCHISE_NOT_FOUND, "franchise.not.found")))
      .flatMap(f -> repo.save(new Franchise(f.id(), name, f.branches())));
  }
}

