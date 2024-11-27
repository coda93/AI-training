package com.example.demo.chat.controller;

import com.example.demo.chat.dto.ChatResponse;
import com.example.demo.chat.service.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatServiceImpl chatService;

    @GetMapping("/chat")
    public ChatResponse chat(@RequestParam String prompt) {
        log.info("Received user input: {}", prompt);
        ChatResponse response = chatService.getChatResponse(prompt);
        log.info("AI response: {}", response);
        return response;
    }
}
