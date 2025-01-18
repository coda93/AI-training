package com.example.demo.embedding.service;

import com.example.demo.embedding.client.pinecone.PineconeProperties;
import com.example.demo.embedding.config.EmbeddingProperties;
import com.example.demo.exception.PineconeApiException;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openapitools.inference.client.ApiException;
import org.openapitools.inference.client.model.Embedding;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final String METADATA_KEY = "text";
    private final EmbeddingProperties embeddingProperties;
    private final PineconeProperties pineconeProperties;
    private final Pinecone pineconeClient;


    public List<Float> generateEmbedding(String inputText) {
        try {
            return getEmbedding(inputText, EmbeddingInputType.PASSAGE);
        } catch (ApiException e) {
            throw new PineconeApiException("Error generating embedding.");
        }
    }

    public List<Float> storeEmbedding(String id, String text) {
        try {
            List<Float> embeddingVector = getEmbedding(text, EmbeddingInputType.PASSAGE);
            Index index = getIndexConnection();
            Struct metadata = Struct.newBuilder()
                    .putFields(METADATA_KEY, Value.newBuilder()
                            .setStringValue(text)
                            .build())
                    .build();
            index.upsert(id, embeddingVector, null, null, metadata, pineconeProperties.getNamespace());
            return embeddingVector;
        } catch (ApiException e) {
            throw new PineconeApiException("Error storing embedding.");
        }
    }

    public QueryResponseWithUnsignedIndices searchClosestEmbeddings(String queryText) {
        try {
            List<Float> queryVector = getEmbedding(queryText, EmbeddingInputType.QUERY);
            Index index = getIndexConnection();
            return index.query(embeddingProperties.getTopK(), queryVector, null, null, null, pineconeProperties.getNamespace(), null, true, true);
        } catch (ApiException e) {
            throw new PineconeApiException("Error occurred while searching for closest embeddings: " + e.getMessage());
        }
    }

    private List<Float> getEmbedding(String text, EmbeddingInputType inputType) throws ApiException {
        String embeddingModel = embeddingProperties.getModel();
        Map<String, Object> parameters = createParams(inputType);
        List<Embedding> embeddings = pineconeClient.getInferenceClient()
                .embed(embeddingModel, parameters, Collections.singletonList(text))
                .getData();
        return convertEmbeddingsToFloatVectors(embeddings);
    }

    private Map<String, Object> createParams(EmbeddingInputType inputType) {
        return Map.of(
                "input_type", inputType.getName(),
                "truncate", "END"
        );
    }

    private List<Float> convertEmbeddingsToFloatVectors(List<Embedding> embeddings) {
        if (embeddings == null || embeddings.isEmpty()) {
            return Collections.emptyList();
        }
        List<BigDecimal> values = embeddings.getFirst()
                .getValues();
        return values == null ? Collections.emptyList() :
                values.stream()
                        .map(BigDecimal::floatValue)
                        .collect(Collectors.toList());
    }

    private Index getIndexConnection() {
        String indexName = pineconeProperties.getIndexName();
        return pineconeClient.getIndexConnection(indexName);
    }

    @AllArgsConstructor
    @Getter
    private enum EmbeddingInputType {
        PASSAGE("passage"),
        QUERY("query");

        private final String name;
    }
}
