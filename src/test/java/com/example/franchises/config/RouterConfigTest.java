package com.example.franchises.config;

import com.example.franchises.web.handler.FranchiseHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class RouterConfigTest {

  @Test
  void registersCreateFranchiseRoute() {
    FranchiseHandler handler = Mockito.mock(FranchiseHandler.class);
    Mockito.when(handler.createFranchise(Mockito.any())).thenReturn(ServerResponse.ok().build());
    RouterFunction<ServerResponse> routes = new RouterConfig().routes(handler);

    MockServerRequest request = MockServerRequest.builder()
        .method(HttpMethod.POST)
        .uri(URI.create("/api/v1/franchises"))
        .build();

    assertTrue(routes.route(request).isPresent());
  }
}

