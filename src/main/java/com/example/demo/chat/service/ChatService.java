package com.example.demo.chat.service;

import com.example.demo.chat.dto.ChatResponse;

public interface ChatService {
    ChatResponse getChatResponse(String prompt);
}
