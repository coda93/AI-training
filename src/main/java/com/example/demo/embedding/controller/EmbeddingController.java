package com.example.demo.embedding.controller;

import com.example.demo.embedding.converter.EmbeddingConverter;
import com.example.demo.embedding.dto.EmbeddingGenerateRequest;
import com.example.demo.embedding.dto.EmbeddingQueryRequest;
import com.example.demo.embedding.dto.EmbeddingSearchResponse;
import com.example.demo.embedding.service.EmbeddingService;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;
    private final EmbeddingConverter embeddingConverter;

    @PostMapping("/build")
    public ResponseEntity<List<Float>> buildEmbedding(@RequestBody EmbeddingGenerateRequest request) {
        return ResponseEntity.ok(embeddingService.generateEmbedding(request.inputText()));
    }

    @PostMapping("/store")
    public ResponseEntity<List<Float>> buildAndStoreEmbedding(@RequestParam String id, @RequestBody EmbeddingGenerateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(embeddingService.buildAndStoreEmbedding(id, request.inputText()));
    }

    @PostMapping("/search")
    public ResponseEntity<EmbeddingSearchResponse> searchClosestEmbeddings(@RequestBody EmbeddingQueryRequest request) {
        QueryResponseWithUnsignedIndices queryResponseWithUnsignedIndices = embeddingService.searchClosestEmbeddings(request.queryText());
        return ResponseEntity.ok(embeddingConverter.convertToEmbeddingSearchResponse(queryResponseWithUnsignedIndices));
    }
}
