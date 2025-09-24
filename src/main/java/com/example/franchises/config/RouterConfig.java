package com.example.franchises.config;

import com.example.franchises.web.handler.FranchiseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {
  //http://54.235.42.244:8080/api/v1/
  @Bean
  public RouterFunction<ServerResponse> routes(FranchiseHandler h) {
    return RouterFunctions.nest(RequestPredicates.path("/api/v1"),
      RouterFunctions.route()
        .POST("/franchises", h::createFranchise)
        .GET("/franchises", h::getAllFranchises)
        .PATCH("/franchises/{franchiseId}/name", h::updateFranchiseName)
        .POST("/franchises/{franchiseId}/branches", h::addBranch)
        .PATCH("/franchises/{franchiseId}/branches/{branchId}/name", h::updateBranchName)
        .POST("/franchises/{franchiseId}/branches/{branchId}/products", h::addProduct)
        .DELETE("/franchises/{franchiseId}/branches/{branchId}/products/{productId}", h::removeProduct)
        .PATCH("/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock", h::updateProductStock)
        .PATCH("/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name", h::updateProductName)
        .GET("/franchises/{franchiseId}/branches/max-stock", h::maxStockPerBranch)
        .build()
    );
  }
}
