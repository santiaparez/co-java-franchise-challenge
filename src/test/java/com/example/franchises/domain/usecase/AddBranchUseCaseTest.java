package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class AddBranchUseCaseTest {

  private final SpringDataFranchiseRepository repo = Mockito.mock(SpringDataFranchiseRepository.class);
  private final AddBranchUseCase useCase = new AddBranchUseCase(repo);

  @Test
  void addsNewBranchAndPersistsFranchise() {
    Franchise existing = new Franchise("f-1", "Acme", java.util.List.of());
    Mockito.when(repo.findById("f-1")).thenReturn(Mono.just(existing));
    Mockito.when(repo.save(Mockito.any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    StepVerifier.create(useCase.execute("f-1", "Downtown"))
        .assertNext(branch -> {
          assertEquals("Downtown", branch.name());
          assertNotNull(branch.id());
        })
        .verifyComplete();

    ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
    Mockito.verify(repo).save(captor.capture());
    assertEquals(1, captor.getValue().branches().size());
    assertEquals("Downtown", captor.getValue().branches().get(0).name());
  }

  @Test
  void failsWhenFranchiseDoesNotExist() {
    Mockito.when(repo.findById("missing")).thenReturn(Mono.empty());

    StepVerifier.create(useCase.execute("missing", "Downtown"))
        .expectErrorSatisfies(ex -> {
          assertInstanceOf(DomainException.class, ex);
          assertEquals(ErrorCodes.FRANCHISE_NOT_FOUND, ((DomainException) ex).getCode());
        })
        .verify();
  }
}

