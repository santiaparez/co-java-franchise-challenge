package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.domain.model.Product;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaxStockPerBranchUseCaseTest {

  private final SpringDataFranchiseRepository repo = Mockito.mock(SpringDataFranchiseRepository.class);
  private final MaxStockPerBranchUseCase useCase = new MaxStockPerBranchUseCase(repo);

  @Test
  void returnsMaxStockPerBranch() {
    Branch withProducts = new Branch("b-1", "Downtown", List.of(
        new Product("p-1", "Soda", 5),
        new Product("p-2", "Juice", 9)
    ));
    Branch empty = new Branch("b-2", "Uptown", List.of());
    Franchise franchise = new Franchise("f-1", "Acme", List.of(withProducts, empty));
    Mockito.when(repo.findById("f-1")).thenReturn(Mono.just(franchise));

    StepVerifier.create(useCase.execute("f-1"))
        .assertNext(result -> {
          assertEquals("b-1", result.branchId());
          assertEquals("Downtown", result.branchName());
          assertEquals("p-2", result.productId());
          assertEquals("Juice", result.productName());
          assertEquals(9, result.stock());
        })
        .assertNext(result -> {
          assertEquals("b-2", result.branchId());
          assertEquals("Uptown", result.branchName());
          assertNull(result.productId());
          assertNull(result.stock());
        })
        .verifyComplete();
  }

  @Test
  void failsWhenFranchiseMissing() {
    Mockito.when(repo.findById("missing")).thenReturn(Mono.empty());

    StepVerifier.create(useCase.execute("missing"))
        .expectErrorSatisfies(ex -> {
          assertInstanceOf(DomainException.class, ex);
          assertEquals(ErrorCodes.FRANCHISE_NOT_FOUND, ((DomainException) ex).getCode());
        })
        .verify();
  }
}

