package com.example.demo.embedding.client.pinecone;

import io.pinecone.clients.Pinecone;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PineconeConfig {

    private final PineconeProperties pineconeProperties;

    @Bean
    public Pinecone pineconeClient() {
        return new Pinecone.Builder(pineconeProperties.getKey()).build();
    }
}
