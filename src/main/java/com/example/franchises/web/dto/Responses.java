package com.example.franchises.web.dto;

import java.util.List;

public class Responses {
  public record IdResponse(String id) {}
  public record ProductResponse(String id, String name, int stock) {}
  public record BranchResponse(String id, String name, List<ProductResponse> products) {}
  public record FranchiseResponse(String id, String name, List<BranchResponse> branches) {}
  public record MaxStockResponse(String branchId, String branchName, String productId, String productName, Integer stock) {}
}
