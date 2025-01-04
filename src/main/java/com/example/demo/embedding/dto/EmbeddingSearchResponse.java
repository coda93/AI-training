package com.example.demo.embedding.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record EmbeddingSearchResponse(
        List<Match> matches,
        String namespace,
        Usage usage
) {

    @Builder
    public record Match(
            double score,
            String id,
            String text
    ) {
    }

    @Builder
    public record Usage(
            int readUnits
    ) {
    }
}
