package com.example.franchises.config;

import com.example.franchises.domain.usecase.*;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UseCasesConfigTest {

  private final UseCasesConfig config = new UseCasesConfig();
  private final SpringDataFranchiseRepository repo = Mockito.mock(SpringDataFranchiseRepository.class);

  @Test
  void createsAllUseCases() {
    assertNotNull(config.createFranchiseUseCase(repo));
    assertNotNull(config.getAllFranchisesUseCase(repo));
    assertNotNull(config.addBranchUseCase(repo));
    assertNotNull(config.addProductUseCase(repo));
    assertNotNull(config.removeProductUseCase(repo));
    assertNotNull(config.updateProductStockUseCase(repo));
    assertNotNull(config.updateFranchiseNameUseCase(repo));
    assertNotNull(config.updateBranchNameUseCase(repo));
    assertNotNull(config.updateProductNameUseCase(repo));
    assertNotNull(config.maxStockPerBranchUseCase(repo));
  }
}

