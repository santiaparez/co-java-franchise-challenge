package com.example.franchises.web;

import com.example.franchises.config.RouterConfig;
import com.example.franchises.domain.usecase.CreateFranchiseUseCase;
import com.example.franchises.web.dto.Requests.CreateFranchiseRequest;
import com.example.franchises.web.handler.FranchiseHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import reactor.core.publisher.Mono;

class FranchiseHandlerTest {
  @Test
  void create_franchise_2xx(){
    var validator = new LocalValidatorFactoryBean(); validator.afterPropertiesSet();
    var create = Mockito.mock(CreateFranchiseUseCase.class);
    var handler = new FranchiseHandler(validator, create,
      Mockito.mock(com.example.franchises.domain.usecase.AddBranchUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.AddProductUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.RemoveProductUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.UpdateProductStockUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.MaxStockPerBranchUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.UpdateFranchiseNameUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.UpdateBranchNameUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.UpdateProductNameUseCase.class),
      Mockito.mock(com.example.franchises.domain.usecase.GetAllFranchisesUseCase.class)
    );
    var router = new RouterConfig().routes(handler);
    var client = WebTestClient.bindToRouterFunction(router).handlerStrategies(HandlerStrategies.withDefaults()).build();

    Mockito.when(create.execute("Acme")).thenReturn(Mono.just(new com.example.franchises.domain.model.Franchise("id-1","Acme", java.util.List.of())));

    client.post().uri("/api/v1/franchises")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateFranchiseRequest("Acme"))
      .exchange()
      .expectStatus().is2xxSuccessful();
  }
}
