package com.example.demo.chat.manger;

import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ChatHistoryManager {

    private ChatHistory chatHistory = new ChatHistory();

    public void addSystemMessage(String message) {
        chatHistory.addSystemMessage(message);
    }

    public void addUserMessage(String message) {
        chatHistory.addUserMessage(message);
    }

    public void addAssistantMessage(String message) {
        chatHistory.addAssistantMessage(message);
    }

    public void resetChatHistory() {
        chatHistory = new ChatHistory();
    }
}

