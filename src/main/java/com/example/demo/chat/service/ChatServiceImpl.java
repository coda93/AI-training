package com.example.demo.chat.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.example.demo.chat.dto.ChatParams;
import com.example.demo.chat.dto.ChatResponse;
import com.example.demo.chat.manger.ChatHistoryManager;
import com.example.demo.chat.manger.PromptExecutionSettingsManager;
import com.example.demo.exception.AIResponseException;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    private final Kernel kernel;
    private final ChatHistoryManager chatHistoryManager;
    private final PromptExecutionSettingsManager promptExecutionSettingsManager;


    public ChatServiceImpl(@Value("${client-openai-key}") String clientOpenAIKey,
                           @Value("${client-openai-endpoint}") String clientOpenAIEndpoint,
                           @Value("${client-openai-deployment-name}") String clientOpenAIDeploymentName) {
        OpenAIAsyncClient openAIAsyncClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(clientOpenAIKey))
                .endpoint(clientOpenAIEndpoint)
                .buildAsyncClient();
        OpenAIChatCompletion chatCompletionService = OpenAIChatCompletion.builder()
                .withModelId(clientOpenAIDeploymentName)
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();
        this.kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
        this.chatHistoryManager = new ChatHistoryManager();
        this.promptExecutionSettingsManager = new PromptExecutionSettingsManager();
    }

    @Override
    public ChatResponse getChatResponse(String prompt) {
        KernelFunction<String> chatFunction = getChatFunction();
        var response = kernel.invokeAsync(chatFunction)
                .withArguments(getKernelFunctionArguments(prompt, chatHistoryManager.getChatHistory()))
                .withPromptExecutionSettings(promptExecutionSettingsManager.getSettings())
                .block();
        chatHistoryManager.addUserMessage(prompt);
        if (response == null) {
            throw new AIResponseException("There is no response from AI. Try with an another prompt");
        }
        chatHistoryManager.addAssistantMessage(response.getResult());
        return ChatResponse.builder()
                .aiResponse(response.getResult())
                .build();
    }

    @Override
    public ChatResponse sendSystemPrompt(String systemPrompt) {
        chatHistoryManager.resetChatHistory();
        chatHistoryManager.addSystemMessage(systemPrompt);
        return ChatResponse.builder()
                .aiResponse("You have just started a new chat with a provided system prompt --> " + systemPrompt)
                .build();
    }

    @Override
    public ChatResponse changePromptExecutionSettings(ChatParams chatParams) {
        promptExecutionSettingsManager.updateSettings(chatParams);
        return ChatResponse.builder()
                .aiResponse("You have just started a new chat with the following parameters --> " + promptExecutionSettingsManager.getPromptExecutionSettingsString())
                .build();
    }

    @Override
    public ChatResponse reset() {
        chatHistoryManager.resetChatHistory();
        promptExecutionSettingsManager.resetPromptExecutionSettings();
        return ChatResponse.builder()
                .aiResponse("You have just started a new chat with clear chat history.")
                .build();
    }

    private KernelFunction<String> getChatFunction() {
        return KernelFunction.<String>createFromPrompt("""
                        {{$chatHistory}}
                        <message role="user">{{$request}}</message>""")
                .build();
    }

    private KernelFunctionArguments getKernelFunctionArguments(String prompt, ChatHistory chatHistory) {
        return KernelFunctionArguments.builder()
                .withVariable("request", prompt)
                .withVariable("chatHistory", chatHistory)
                .build();
    }
}
