package com.example.franchises.domain.usecase;

import com.example.franchises.domain.model.Franchise;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class GetAllFranchisesUseCaseTest {

  private final SpringDataFranchiseRepository repo = Mockito.mock(SpringDataFranchiseRepository.class);
  private final GetAllFranchisesUseCase useCase = new GetAllFranchisesUseCase(repo);

  @Test
  void delegatesToRepository() {
    Franchise franchise = new Franchise("f-1", "Acme", java.util.List.of());
    Mockito.when(repo.findAll()).thenReturn(Flux.just(franchise));

    StepVerifier.create(useCase.execute())
        .expectNext(franchise)
        .verifyComplete();

    Mockito.verify(repo).findAll();
  }
}

