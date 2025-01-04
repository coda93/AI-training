package com.example.demo.embedding.service;

import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;

import java.util.List;


public interface EmbeddingService {

    List<Float> generateEmbedding(String inputText);

    List<Float> storeEmbedding(String id, String text);

    QueryResponseWithUnsignedIndices searchClosestEmbeddings(String queryText);
}
