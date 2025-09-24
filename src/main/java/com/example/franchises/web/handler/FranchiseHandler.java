package com.example.franchises.web.handler;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.usecase.*;
import com.example.franchises.web.dto.Requests.*;
import com.example.franchises.web.dto.Responses.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class FranchiseHandler {
  private final Validator validator;
  private final CreateFranchiseUseCase createFranchise;
  private final AddBranchUseCase addBranch;
  private final AddProductUseCase addProduct;
  private final RemoveProductUseCase removeProduct;
  private final UpdateProductStockUseCase updateStock;
  private final MaxStockPerBranchUseCase maxStock;
  private final UpdateFranchiseNameUseCase updateFranchiseName;
  private final UpdateBranchNameUseCase updateBranchName;
  private final UpdateProductNameUseCase updateProductName;
  private final GetAllFranchisesUseCase getAllFranchises;

  public FranchiseHandler(Validator validator, CreateFranchiseUseCase createFranchise, AddBranchUseCase addBranch,
                          AddProductUseCase addProduct, RemoveProductUseCase removeProduct, UpdateProductStockUseCase updateStock,
                          MaxStockPerBranchUseCase maxStock, UpdateFranchiseNameUseCase updateFranchiseName,
                          UpdateBranchNameUseCase updateBranchName, UpdateProductNameUseCase updateProductName, GetAllFranchisesUseCase getAllFranchises) {
    this.validator = validator; this.createFranchise = createFranchise; this.addBranch = addBranch; this.addProduct = addProduct;
    this.removeProduct = removeProduct; this.updateStock = updateStock; this.maxStock = maxStock; this.updateFranchiseName = updateFranchiseName;
    this.updateBranchName = updateBranchName; this.updateProductName = updateProductName; this.getAllFranchises = getAllFranchises;
  }

  public Mono<ServerResponse> createFranchise(ServerRequest req){
    return validatedBody(req, CreateFranchiseRequest.class, body ->
      createFranchise.execute(body.name()).flatMap(f -> okJson(new IdResponse(f.id())))
    );
  }

  public Mono<ServerResponse> updateFranchiseName(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId");
    return validatedBody(req, UpdateNameRequest.class, body ->
      updateFranchiseName.execute(franchiseId, body.name()).flatMap(f -> okJson(Mapper.franchise(f)))
    );
  }

  public Mono<ServerResponse> addBranch(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId");
    return validatedBody(req, AddBranchRequest.class, body ->
      addBranch.execute(franchiseId, body.name()).flatMap(b -> okJson(new IdResponse(b.id())))
    );
  }

  public Mono<ServerResponse> updateBranchName(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId"); String branchId = req.pathVariable("branchId");
    return validatedBody(req, UpdateNameRequest.class, body ->
      updateBranchName.execute(franchiseId, branchId, body.name()).flatMap(b -> okJson(Mapper.branch(b)))
    );
  }

  public Mono<ServerResponse> addProduct(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId"); String branchId = req.pathVariable("branchId");
    return validatedBody(req, AddProductRequest.class, body ->
      addProduct.execute(franchiseId, branchId, body.name(), body.stock()).flatMap(p -> okJson(new IdResponse(p.id())))
    );
  }

  public Mono<ServerResponse> removeProduct(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId"); String branchId = req.pathVariable("branchId"); String productId = req.pathVariable("productId");
    return removeProduct.execute(franchiseId, branchId, productId).then(ServerResponse.noContent().build());
  }

  public Mono<ServerResponse> updateProductStock(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId"); String branchId = req.pathVariable("branchId"); String productId = req.pathVariable("productId");
    return validatedBody(req, UpdateStockRequest.class, body ->
      updateStock.execute(franchiseId, branchId, productId, body.stock()).flatMap(p -> okJson(Mapper.product(p)))
    );
  }

  public Mono<ServerResponse> updateProductName(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId"); String branchId = req.pathVariable("branchId"); String productId = req.pathVariable("productId");
    return validatedBody(req, UpdateNameRequest.class, body ->
      updateProductName.execute(franchiseId, branchId, productId, body.name()).flatMap(p -> okJson(Mapper.product(p)))
    );
  }

  public Mono<ServerResponse> maxStockPerBranch(ServerRequest req){
    String franchiseId = req.pathVariable("franchiseId");
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
      .body(maxStock.execute(franchiseId).map(r -> new MaxStockResponse(r.branchId(), r.branchName(), r.productId(), r.productName(), r.stock())), MaxStockResponse.class);
  }

  public Mono<ServerResponse> getAllFranchises(ServerRequest req) {
    return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(getAllFranchises.execute().map(Mapper::franchise), FranchiseResponse.class);
  }


  // helpers
  private static class Mapper {
    static FranchiseResponse franchise(com.example.franchises.domain.model.Franchise f){
      return new FranchiseResponse(
        f.id(), f.name(),
        f.branches().stream().map(Mapper::branch).toList()
      );
    }
    static BranchResponse branch(com.example.franchises.domain.model.Branch b){
      return new BranchResponse(
        b.id(), b.name(), b.products().stream().map(Mapper::product).toList()
      );
    }
    static ProductResponse product(com.example.franchises.domain.model.Product p){
      return new ProductResponse(p.id(), p.name(), p.stock());
    }
  }

  private <T> Mono<ServerResponse> validatedBody(ServerRequest req, Class<T> clazz, java.util.function.Function<T, Mono<ServerResponse>> fn){
    return req.bodyToMono(clazz).flatMap(body -> {
      var errors = new BeanPropertyBindingResult(body, clazz.getSimpleName());
      validator.validate(body, errors);
      if (errors.hasErrors()) {
        String msg = errors.getAllErrors().stream().map(e -> e.getDefaultMessage()==null?"validation.error":e.getDefaultMessage()).reduce((a,b) -> a+","+b).orElse("validation.error");
        return problem(400, msg);
      }
      return fn.apply(body);
    }).onErrorResume(DomainException.class, ex -> problem(mapHttp(ex.getCode()), ex.getMessage()));
  }

  private Mono<ServerResponse> okJson(Object any){
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromValue(any));
  }
  private Mono<ServerResponse> problem(int status, String message){
    return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).body(fromValue(java.util.Map.of("message", message)));
  }
  private int mapHttp(com.example.franchises.domain.error.ErrorCodes code){
    return switch (code){
      case FRANCHISE_NOT_FOUND, BRANCH_NOT_FOUND, PRODUCT_NOT_FOUND -> 404;
      case VALIDATION_ERROR -> 400;
      case CONFLICT -> 409;
      default -> 500;
    };
  }
}
