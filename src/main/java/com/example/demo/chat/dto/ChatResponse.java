package com.example.demo.chat.dto;

import lombok.Builder;

@Builder
public record ChatResponse(String aiResponse) {
}
