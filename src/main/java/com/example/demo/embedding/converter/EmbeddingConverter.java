package com.example.demo.embedding.converter;

import com.example.demo.embedding.dto.EmbeddingSearchResponse;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;

import java.util.List;

public interface EmbeddingConverter {

    EmbeddingSearchResponse convertToEmbeddingSearchResponse(QueryResponseWithUnsignedIndices response);

    List<String> convertToListOfClosestEmbeddings(QueryResponseWithUnsignedIndices response);
}
