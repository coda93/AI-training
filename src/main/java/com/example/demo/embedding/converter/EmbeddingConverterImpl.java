package com.example.demo.embedding.converter;

import com.example.demo.embedding.dto.EmbeddingSearchResponse;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingConverterImpl implements EmbeddingConverter {

    public EmbeddingSearchResponse convertToEmbeddingSearchResponse(QueryResponseWithUnsignedIndices response) {
        return EmbeddingSearchResponse.builder()
                .matches(response.getMatchesList()
                        .stream()
                        .map(match -> EmbeddingSearchResponse.Match.builder()
                                .score(match.getScore())
                                .id(match.getId())
                                .text(match.getMetadata()
                                        .getFieldsMap()
                                        .values()
                                        .iterator()
                                        .next()
                                        .getStringValue())
                                .build())
                        .toList())
                .namespace(response.getNamespace())
                .usage(EmbeddingSearchResponse.Usage.builder()
                        .readUnits(response.getUsage()
                                .getReadUnits())
                        .build())
                .build();
    }

    public List<String> convertToListOfClosestEmbeddings(QueryResponseWithUnsignedIndices response) {
        return response.getMatchesList()
                .stream()
                .map(match -> match.getMetadata()
                        .getFieldsMap()
                        .values()
                        .iterator()
                        .next()
                        .getStringValue())
                .toList();
    }
}
