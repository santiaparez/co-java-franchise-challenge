package com.example.franchises.infrastructure.persistence;

import com.example.franchises.infrastructure.repository.documents.FranchiseItem;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class DynamoConfigTest {

  private final DynamoConfig config = new DynamoConfig();

  @Test
  void createsDynamoClient() {
    assertNotNull(config.dynamoClient("us-east-1", ""));
  }

  @Test
  void buildsFranchiseTable() {
    DynamoDbEnhancedAsyncClient enhanced = Mockito.mock(DynamoDbEnhancedAsyncClient.class);
    DynamoDbAsyncClient dynamo = Mockito.mock(DynamoDbAsyncClient.class);
    @SuppressWarnings("unchecked")
    DynamoDbAsyncTable<FranchiseItem> table = Mockito.mock(DynamoDbAsyncTable.class);
    Mockito.when(enhanced.table(Mockito.eq("franchises"), Mockito.any(TableSchema.class))).thenReturn(table);
    DescribeTableRequest request = DescribeTableRequest.builder().tableName("franchises").build();
    DescribeTableResponse response = DescribeTableResponse.builder()
        .table(TableDescription.builder().tableName("franchises").build())
        .build();
    Mockito.when(dynamo.describeTable(request)).thenReturn(CompletableFuture.completedFuture(response));

    DynamoDbAsyncTable<FranchiseItem> result = config.franchiseTable(enhanced, dynamo, "franchises");

    assertSame(table, result);
    Mockito.verify(dynamo).describeTable(request);
  }
}

