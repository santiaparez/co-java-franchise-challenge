package com.example.franchises.usecase;

import com.example.franchises.domain.model.Franchise;
import com.example.franchises.domain.usecase.CreateFranchiseUseCase;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;

class CreateFranchiseUseCaseTest {
  @Test
  void create_ok(){
    SpringDataFranchiseRepository repo = Mockito.mock(SpringDataFranchiseRepository.class);
    Mockito.when(repo.save(Mockito.any(Franchise.class))).thenAnswer(i -> Mono.just((Franchise) i.getArguments()[0]));
    var uc = new CreateFranchiseUseCase(repo);
    var res = uc.execute("My Franchise").block();
    assertNotNull(res);
    assertEquals("My Franchise", res.name());
  }
}
