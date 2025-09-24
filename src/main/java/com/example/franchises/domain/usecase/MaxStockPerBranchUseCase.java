package com.example.franchises.domain.usecase;

import com.example.franchises.domain.error.DomainException;
import com.example.franchises.domain.error.ErrorCodes;
import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Product;
import com.example.franchises.infrastructure.repository.SpringDataFranchiseRepository;
import reactor.core.publisher.Flux;

import java.util.Comparator;

public class MaxStockPerBranchUseCase {

    private final SpringDataFranchiseRepository repo;

    public MaxStockPerBranchUseCase(SpringDataFranchiseRepository repo) {
        this.repo = repo;
    }

    /** Resultado que expone el handler (IDs/nombres y stock máximo por sucursal). */
    public record Result(String branchId, String branchName, String productId, String productName, Integer stock) {}

    /** Para una franquicia dada, devuelve (por cada sucursal) el producto con mayor stock. */
    public Flux<Result> execute(String franchiseId) {
        return repo.findById(franchiseId)
                .switchIfEmpty(reactor.core.publisher.Mono.error(
                        new DomainException(ErrorCodes.FRANCHISE_NOT_FOUND, "franchise.not.found")))
                .flatMapMany(f ->
                        Flux.fromIterable(f.branches())
                                .map(this::mapBranchMax)
                );
    }

    private Result mapBranchMax(Branch b) {
        Product max = b.products().stream()
                .max(Comparator.comparingInt(Product::stock))
                .orElse(null);
        return new Result(
                b.id(),
                b.name(),
                max == null ? null : max.id(),
                max == null ? null : max.name(),
                max == null ? null : max.stock()
        );
    }
}
