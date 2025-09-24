package com.example.franchises.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Requests {
  public record CreateFranchiseRequest(@NotBlank String name) {}
  public record UpdateNameRequest(@NotBlank String name) {}
  public record AddBranchRequest(@NotBlank String name) {}
  public record AddProductRequest(@NotBlank String name, @Min(0) int stock) {}
  public record UpdateStockRequest(@Min(0) int stock) {}
}
