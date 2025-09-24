package com.example.franchises.domain.usecase;

import com.example.franchises.domain.model.Franchise;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public class CreateFranchiseUseCase {
  private final SpringDataFranchiseRepository repo;
  public CreateFranchiseUseCase(SpringDataFranchiseRepository repo) { this.repo = repo; }
  public Mono<Franchise> execute(String name) {
    return repo.save(new Franchise(UUID.randomUUID().toString(), name, java.util.List.of()));
  }
}
