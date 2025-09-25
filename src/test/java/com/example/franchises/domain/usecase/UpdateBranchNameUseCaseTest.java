package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.domain.model.Product;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateBranchNameUseCaseTest {

  private final SpringDataFranchiseRepository repo = Mockito.mock(SpringDataFranchiseRepository.class);
  private final UpdateBranchNameUseCase useCase = new UpdateBranchNameUseCase(repo);

  private static Franchise franchiseWithBranch() {
    Branch branch = new Branch("b-1", "Downtown", List.of(new Product("p-1", "Soda", 5)));
    return new Franchise("f-1", "Acme", List.of(branch));
  }

  @Test
  void updatesBranchName() {
    Mockito.when(repo.findById("f-1")).thenReturn(Mono.just(franchiseWithBranch()));
    Mockito.when(repo.save(Mockito.any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    StepVerifier.create(useCase.execute("f-1", "b-1", "Uptown"))
        .assertNext(branch -> {
          assertEquals("b-1", branch.id());
          assertEquals("Uptown", branch.name());
        })
        .verifyComplete();

    ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
    Mockito.verify(repo).save(captor.capture());
    assertEquals("Uptown", captor.getValue().branches().get(0).name());
  }

  @Test
  void failsWhenBranchMissing() {
    Franchise franchise = new Franchise("f-1", "Acme", List.of(new Branch("other", "Other", List.of())));
    Mockito.when(repo.findById("f-1")).thenReturn(Mono.just(franchise));

    StepVerifier.create(useCase.execute("f-1", "b-1", "Uptown"))
        .expectErrorSatisfies(ex -> {
          assertInstanceOf(DomainException.class, ex);
          assertEquals(ErrorCodes.BRANCH_NOT_FOUND, ((DomainException) ex).getCode());
        })
        .verify();
  }

  @Test
  void failsWhenFranchiseMissing() {
    Mockito.when(repo.findById("missing")).thenReturn(Mono.empty());

    StepVerifier.create(useCase.execute("missing", "b-1", "Uptown"))
        .expectErrorSatisfies(ex -> {
          assertInstanceOf(DomainException.class, ex);
          assertEquals(ErrorCodes.FRANCHISE_NOT_FOUND, ((DomainException) ex).getCode());
        })
        .verify();
  }
}

