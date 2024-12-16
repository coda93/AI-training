package com.example.demo.chat.dto;

import com.example.demo.exception.InvalidChatParamException;

import java.util.List;
import java.util.Optional;

public record ChatParams(
        Optional<Double> temperature,
        Optional<Double> topP,
        Optional<Double> presencePenalty,
        Optional<Double> frequencyPenalty,
        Optional<Integer> maxTokens,
        Optional<Integer> bestOf,
        Optional<List<String>> stopSequences
) {

    // Validate constraints during construction
    public ChatParams {
        temperature.ifPresent(t -> {
            if (t < 0.0 || t > 2.0) {
                throw new InvalidChatParamException("Temperature must be between 0.0 and 2.0");
            }
        });
        topP.ifPresent(t -> {
            if (t < 0.0 || t > 1.0) {
                throw new InvalidChatParamException("TopP must be between 0.0 and 1.0");
            }
        });
        presencePenalty.ifPresent(p -> {
            if (p < -2.0 || p > 2.0) {
                throw new InvalidChatParamException("PresencePenalty must be between -2.0 and 2.0");
            }
        });
        frequencyPenalty.ifPresent(p -> {
            if (p < -2.0 || p > 2.0) {
                throw new InvalidChatParamException("FrequencyPenalty must be between -2.0 and 2.0");
            }
        });
        maxTokens.ifPresent(t -> {
            if (t < 1 || t > 4096) {
                throw new InvalidChatParamException("MaxTokens must be between 1 and 4096");
            }
        });
        bestOf.ifPresent(b -> {
            if (b < 1 || b > 10) {
                throw new InvalidChatParamException("BestOf must be between 1 and 10");
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ChatParams{");
        temperature.ifPresent(t -> sb.append("temperature=").append(t).append(", "));
        topP.ifPresent(t -> sb.append("topP=").append(t).append(", "));
        presencePenalty.ifPresent(p -> sb.append("presencePenalty=").append(p).append(", "));
        frequencyPenalty.ifPresent(p -> sb.append("frequencyPenalty=").append(p).append(", "));
        maxTokens.ifPresent(t -> sb.append("maxTokens=").append(t).append(", "));
        bestOf.ifPresent(b -> sb.append("bestOf=").append(b).append(", "));
        stopSequences.ifPresent(s -> sb.append("stopSequences=").append(s).append(", "));
        if (sb.length() > 10) {
            sb.setLength(sb.length() - 2);
        }
        sb.append('}');
        return sb.toString();
    }
}
