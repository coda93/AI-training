package com.example.demo.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record ChatResponse(

        String aiResponse,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String model
) {
}
