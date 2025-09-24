package com.example.franchises.infrastructure.persistence;

import com.example.franchises.infrastructure.repository.documents.FranchiseItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.concurrent.CompletionException;

@Configuration
public class DynamoConfig {

    @Bean
    DynamoDbAsyncClient dynamoClient(
            @Value("${app.dynamodb.region}") String region,
            @Value("${app.dynamodb.endpoint-override:}") String endpointOverride
    ) {
        var builder = DynamoDbAsyncClient.builder()
                .region(Region.of(region))
                .httpClientBuilder(NettyNioAsyncHttpClient.builder())
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (endpointOverride != null && !endpointOverride.isBlank()) {
            builder.endpointOverride(URI.create(endpointOverride));
        }
        return builder.build();
    }

    @Bean
    DynamoDbEnhancedAsyncClient enhanced(DynamoDbAsyncClient client) {
        return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(client).build();
    }

    @Bean
    DynamoDbAsyncTable<FranchiseItem> franchiseTable(
            DynamoDbEnhancedAsyncClient enhanced,
            DynamoDbAsyncClient dynamo,
            @Value("${app.dynamodb.table-name}") String tableName
    ) {
        var table = enhanced.table(tableName, TableSchema.fromBean(FranchiseItem.class));

        // Crea la tabla si no existe (útil en local con LocalStack)
        var describeReq = DescribeTableRequest.builder().tableName(tableName).build();
        dynamo.describeTable(describeReq).exceptionally(ex -> {
            Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
            if (cause instanceof ResourceNotFoundException) {
                var createReq = CreateTableRequest.builder()
                        .tableName(tableName)
                        .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                        .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                        .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                        .build();
                // crear y volver a describir para devolver el mismo tipo de respuesta
                dynamo.createTable(createReq).join();
                return dynamo.describeTable(describeReq).join();
            }
            throw new CompletionException(cause);
        }).join();

        return table;
    }
}
