package com.example.franchises.infrastructure.repository.documents;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import java.util.List;

@DynamoDbBean
public class FranchiseItem {
    private String id;
    private String name;
    private List<BranchItem> branches;

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<BranchItem> getBranches() { return branches; }
    public void setBranches(List<BranchItem> branches) { this.branches = branches; }

    // Anidados simples (se serializan como mapas/listas)
    @DynamoDbBean
    public static class BranchItem {
        private String id; private String name; private List<ProductItem> products;
        public String getId() { return id; } public void setId(String id) { this.id = id; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public List<ProductItem> getProducts() { return products; } public void setProducts(List<ProductItem> products) { this.products = products; }
    }
    @DynamoDbBean
    public static class ProductItem {
        private String id; private String name; private int stock;
        public String getId() { return id; } public void setId(String id) { this.id = id; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public int getStock() { return stock; } public void setStock(int stock) { this.stock = stock; }
    }
}

