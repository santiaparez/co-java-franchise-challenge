package com.example.franchises.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

  @Test
  void buildsApiInfo() {
    OpenAPI api = new OpenApiConfig().customOpenAPI();

    Info info = api.getInfo();
    assertNotNull(info);
    assertEquals("Franchises API", info.getTitle());
    assertEquals("1.0.0", info.getVersion());
  }
}

