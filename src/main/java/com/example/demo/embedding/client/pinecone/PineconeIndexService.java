package com.example.demo.embedding.client.pinecone;

import io.pinecone.clients.Pinecone;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.db_control.client.model.DeletionProtection;
import org.openapitools.db_control.client.model.IndexModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PineconeIndexService {

    private final PineconeProperties pineconeProperties;
    private final Pinecone pineconeClient;

    @PostConstruct
    public void createIndexIfNeeded() {
        String indexName = pineconeProperties.getIndexName();
        if (!indexAlreadyExists(indexName)) {
            String similarityMetric = pineconeProperties.getSimilarityMetric();
            int dimension = pineconeProperties.getDimension();
            String cloudProvider = pineconeProperties.getCloudProvider();
            String region = pineconeProperties.getRegion();
            pineconeClient.createServerlessIndex(indexName, similarityMetric, dimension, cloudProvider, region, DeletionProtection.DISABLED);
            log.info("Index created successfully!");
        }
    }

    private boolean indexAlreadyExists(String indexName) {
        List<IndexModel> indexes = pineconeClient.listIndexes().getIndexes();
        return indexes != null && indexes.stream()
                .anyMatch(index -> index.getName().equals(indexName));
    }
}
