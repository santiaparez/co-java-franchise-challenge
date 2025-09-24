package com.example.franchises.web;

import com.example.franchises.config.RouterConfig;
import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.domain.model.Product;
import com.example.franchises.domain.usecase.*;
import com.example.franchises.web.dto.Requests.*;
import com.example.franchises.web.handler.FranchiseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

class FranchiseHandlerTest {

  private final CreateFranchiseUseCase create = Mockito.mock(CreateFranchiseUseCase.class);
  private final AddBranchUseCase addBranch = Mockito.mock(AddBranchUseCase.class);
  private final AddProductUseCase addProduct = Mockito.mock(AddProductUseCase.class);
  private final RemoveProductUseCase removeProduct = Mockito.mock(RemoveProductUseCase.class);
  private final UpdateProductStockUseCase updateStock = Mockito.mock(UpdateProductStockUseCase.class);
  private final MaxStockPerBranchUseCase maxStock = Mockito.mock(MaxStockPerBranchUseCase.class);
  private final UpdateFranchiseNameUseCase updateFranchiseName = Mockito.mock(UpdateFranchiseNameUseCase.class);
  private final UpdateBranchNameUseCase updateBranchName = Mockito.mock(UpdateBranchNameUseCase.class);
  private final UpdateProductNameUseCase updateProductName = Mockito.mock(UpdateProductNameUseCase.class);
  private final GetAllFranchisesUseCase getAll = Mockito.mock(GetAllFranchisesUseCase.class);

  private WebTestClient client;

  @BeforeEach
  void setUp() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    FranchiseHandler handler = new FranchiseHandler(
        validator,
        create,
        addBranch,
        addProduct,
        removeProduct,
        updateStock,
        maxStock,
        updateFranchiseName,
        updateBranchName,
        updateProductName,
        getAll
    );
    client = WebTestClient.bindToRouterFunction(new RouterConfig().routes(handler))
        .handlerStrategies(HandlerStrategies.withDefaults())
        .configureClient()
        .baseUrl("/api/v1")
        .build();
  }

  @Test
  void createFranchise_success() {
    Mockito.when(create.execute("Acme")).thenReturn(Mono.just(new Franchise("id-1", "Acme", List.of())));

    client.post().uri("/franchises")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new CreateFranchiseRequest("Acme"))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo("id-1");
  }

  @Test
  void createFranchise_validationError() {
    client.post().uri("/franchises")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new CreateFranchiseRequest(""))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.message").isEqualTo("must not be blank");
  }

  @Test
  void updateProductName_mapsDomainExceptionToStatus() {
    Mockito.when(updateProductName.execute("f-1", "b-1", "p-1", "Juice"))
        .thenReturn(Mono.error(new DomainException(ErrorCodes.PRODUCT_NOT_FOUND, "product.not.found")));

    client.patch().uri("/franchises/f-1/branches/b-1/products/p-1/name")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new UpdateNameRequest("Juice"))
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.message").isEqualTo("product.not.found");
  }

  @Test
  void removeProduct_returnsNoContent() {
    Mockito.when(removeProduct.execute("f-1", "b-1", "p-1")).thenReturn(Mono.empty());

    client.delete().uri("/franchises/f-1/branches/b-1/products/p-1")
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  void getAllFranchises_returnsMappedBody() {
    Franchise franchise = new Franchise("f-1", "Acme", List.of(new Branch("b-1", "Downtown", List.of(new Product("p-1", "Soda", 5)))));
    Mockito.when(getAll.execute()).thenReturn(Flux.just(franchise));

    client.get().uri("/franchises")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].branches[0].products[0].name").isEqualTo("Soda");
  }

  @Test
  void maxStockPerBranch_returnsResponse() {
    Mockito.when(maxStock.execute("f-1")).thenReturn(Flux.just(new MaxStockPerBranchUseCase.Result("b-1", "Downtown", "p-1", "Soda", 5)));

    client.get().uri("/franchises/f-1/branches/max-stock")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].stock").isEqualTo(5);
  }
}

