package com.example.demo.chat.controller;

import com.example.demo.chat.dto.ChatResponse;
import com.example.demo.chat.dto.ChatParams;
import com.example.demo.chat.service.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private static final String NEW_CHAT_DELIMITER_IN_LOGS = "------------------------------------------------------";
    private static final String NEW_SETTINGS_DELIMITER_IN_LOGS = "******************************************************";
    private final ChatServiceImpl chatService;

    @GetMapping
    public ChatResponse chat(@RequestParam String prompt) {
        log.info("Received user input: {}", prompt);
        ChatResponse response = chatService.getChatResponse(prompt);
        log.info("AI response: {}", response);
        return response;
    }

    @GetMapping("/system-prompt")
    public ChatResponse sendSystemPrompt(@RequestParam String systemPrompt) {
        printChangeSettingsLog("Starting a new conversation with set the following system prompt: " + systemPrompt);
        return chatService.sendSystemPrompt(systemPrompt);
    }

    @PostMapping("/settings")
    public ChatResponse sendSystemPrompt(@RequestBody ChatParams promptExecutionSettings) {
        printChangeSettingsLog("Starting a new conversation with set the following parameters: " + promptExecutionSettings);
        return chatService.changePromptExecutionSettings(promptExecutionSettings);
    }

    @PutMapping("/model")
    public ChatResponse changeModel() {
        ChatResponse response = chatService.changeModel();
        printChangeSettingsLog(response.aiResponse());
        return response;
    }

    @DeleteMapping("/reset")
    public ChatResponse reset() {
        log.info(NEW_CHAT_DELIMITER_IN_LOGS);
        log.info("You have just started a new chat with clear chat history and default prompt execution settings.");
        return chatService.reset();
    }

    private void printChangeSettingsLog(String info) {
        log.warn(NEW_SETTINGS_DELIMITER_IN_LOGS);
        log.info(info);
    }
}
