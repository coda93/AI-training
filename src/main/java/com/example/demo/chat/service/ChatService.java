package com.example.demo.chat.service;

import com.example.demo.chat.dto.ChatResponse;
import com.example.demo.chat.dto.ChatParams;

public interface ChatService {
    ChatResponse getChatResponse(String prompt);

    ChatResponse sendSystemPrompt(String systemPrompt);

    ChatResponse changePromptExecutionSettings(ChatParams promptExecutionSettings);

    ChatResponse changeModel();

    ChatResponse reset();
}
