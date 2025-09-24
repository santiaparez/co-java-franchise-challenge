package com.example.franchises.config;

import com.example.franchises.domain.usecase.*;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(SpringDataFranchiseRepository repo) {
        return new CreateFranchiseUseCase(repo);
    }

    @Bean
    public GetAllFranchisesUseCase getAllFranchisesUseCase(SpringDataFranchiseRepository repo) {
        return new GetAllFranchisesUseCase(repo);
    }


    @Bean
    public AddBranchUseCase addBranchUseCase(SpringDataFranchiseRepository repo) {
        return new AddBranchUseCase(repo);
    }

    @Bean
    public AddProductUseCase addProductUseCase(SpringDataFranchiseRepository repo) {
        return new AddProductUseCase(repo);
    }

    @Bean
    public RemoveProductUseCase removeProductUseCase(SpringDataFranchiseRepository repo) {
        return new RemoveProductUseCase(repo);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(SpringDataFranchiseRepository repo) {
        return new UpdateProductStockUseCase(repo);
    }

    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(SpringDataFranchiseRepository repo) {
        return new UpdateFranchiseNameUseCase(repo);
    }

    @Bean
    public UpdateBranchNameUseCase updateBranchNameUseCase(SpringDataFranchiseRepository repo) {
        return new UpdateBranchNameUseCase(repo);
    }

    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(SpringDataFranchiseRepository repo) {
        return new UpdateProductNameUseCase(repo);
    }

    @Bean
    public MaxStockPerBranchUseCase maxStockPerBranchUseCase(SpringDataFranchiseRepository repo) {
        return new MaxStockPerBranchUseCase(repo);
    }
}

