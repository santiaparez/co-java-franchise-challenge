package com.example.franchises.domain.usecase;

import com.example.franchises.domain.model.Franchise;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Flux;

public class GetAllFranchisesUseCase {
    private final SpringDataFranchiseRepository repo;

    public GetAllFranchisesUseCase(SpringDataFranchiseRepository repo) {
        this.repo = repo;
    }

    public Flux<Franchise> execute() {
        return repo.findAll();
    }
}

