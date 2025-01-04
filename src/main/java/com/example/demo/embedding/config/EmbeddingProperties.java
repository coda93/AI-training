package com.example.demo.embedding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "embedding")
@Getter
@Setter
public class EmbeddingProperties {

    private String model;
    private int topK;
}
