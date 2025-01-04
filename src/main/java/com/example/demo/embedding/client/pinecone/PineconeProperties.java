package com.example.demo.embedding.client.pinecone;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pinecone")
@Getter
@Setter
public class PineconeProperties {
    private String key;
    private String indexName;
    private String similarityMetric;
    private int dimension;
    private String cloudProvider;
    private String region;
    private String namespace;
}
