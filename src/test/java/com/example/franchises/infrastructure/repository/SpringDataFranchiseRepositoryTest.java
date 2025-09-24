package com.example.franchises.infrastructure.repository;

import com.example.franchises.domain.model.Branch;
import com.example.franchises.domain.model.Franchise;
import com.example.franchises.domain.model.Product;
import com.example.franchises.infrastructure.repository.documents.FranchiseItem;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class SpringDataFranchiseRepositoryTest {

  private final DynamoDbAsyncTable<FranchiseItem> table = Mockito.mock(DynamoDbAsyncTable.class);
  private final SpringDataFranchiseRepository repository = new SpringDataFranchiseRepository(table);

  private static FranchiseItem sampleItem() {
    FranchiseItem.ProductItem productItem = new FranchiseItem.ProductItem();
    productItem.setId("p-1");
    productItem.setName("Soda");
    productItem.setStock(5);

    FranchiseItem.BranchItem branchItem = new FranchiseItem.BranchItem();
    branchItem.setId("b-1");
    branchItem.setName("Downtown");
    branchItem.setProducts(List.of(productItem));

    FranchiseItem item = new FranchiseItem();
    item.setId("f-1");
    item.setName("Acme");
    item.setBranches(List.of(branchItem));
    return item;
  }

  @Test
  void findByIdMapsResult() {
    Mockito.when(table.getItem(Mockito.any())).thenReturn(CompletableFuture.completedFuture(sampleItem()));

    StepVerifier.create(repository.findById("f-1"))
        .assertNext(franchise -> {
          assertEquals("f-1", franchise.id());
          assertEquals("Acme", franchise.name());
          assertEquals("Downtown", franchise.branches().getFirst().name());
        })
        .verifyComplete();
  }

  @Test
  void savePersistsThroughTable() {
    Mockito.when(table.putItem(Mockito.any(FranchiseItem.class))).thenReturn(CompletableFuture.completedFuture(sampleItem()));
    Franchise franchise = new Franchise("f-1", "Acme", List.of(new Branch("b-1", "Downtown", List.of(new Product("p-1", "Soda", 5)))));

    StepVerifier.create(repository.save(franchise))
        .expectNext(franchise)
        .verifyComplete();

    ArgumentCaptor<FranchiseItem> captor = ArgumentCaptor.forClass(FranchiseItem.class);
    Mockito.verify(table).putItem(captor.capture());
    assertEquals("Acme", captor.getValue().getName());
  }

  @Test
  void findAllStreamsFromScan() {
    FranchiseItem item = sampleItem();
    PagePublisher<FranchiseItem> publisher = Mockito.mock(PagePublisher.class);
    Mockito.when(publisher.items()).thenReturn(SdkPublisher.adapt(reactor.core.publisher.Flux.just(item)));
    Mockito.when(table.scan()).thenReturn(publisher);

    StepVerifier.create(repository.findAll())
        .assertNext(franchise -> assertEquals("Acme", franchise.name()))
        .verifyComplete();

    Mockito.verify(table).scan();
  }
}

