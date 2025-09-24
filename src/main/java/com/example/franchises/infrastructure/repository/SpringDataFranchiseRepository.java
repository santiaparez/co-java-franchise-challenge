package com.example.franchises.infrastructure.repository;

import com.example.franchises.domain.model.Franchise;
import com.example.franchises.infrastructure.mapper.FranchiseMapper;
import com.example.franchises.infrastructure.repository.documents.FranchiseItem;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;

@Repository
public class SpringDataFranchiseRepository {

  private final DynamoDbAsyncTable<FranchiseItem> table;

  public SpringDataFranchiseRepository(DynamoDbAsyncTable<FranchiseItem> table) {
    this.table = table;
  }

  public Mono<Franchise> findById(String id){
    return Mono.fromCompletionStage(table.getItem(r -> r.key(k -> k.partitionValue(id))))
            .map(FranchiseMapper::toDomain);
  }

  public Mono<Franchise> save(Franchise franchise){
    var item = FranchiseMapper.toItem(franchise);
    return Mono.fromCompletionStage(table.putItem(item)).thenReturn(franchise);
  }

  public Flux<Franchise> findAll(){
    return Flux.from(table.scan().items()).map(FranchiseMapper::toDomain);
  }
}

