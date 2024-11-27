package com.example.demo.chat.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.example.demo.chat.dto.ChatResponse;
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
    }

    public ChatResponse getChatResponse(String prompt) {
        ChatHistory chatHistory = new ChatHistory();
        KernelFunction<String> chatFunction = getChatFunction();
        var response = kernel.invokeAsync(chatFunction)
                .withArguments(getKernelFunctionArguments(prompt, chatHistory))
                .block();
        chatHistory.addUserMessage(prompt);

        if (response == null) {
            throw new AIResponseException("There is no response from AI. Try with an another prompt");
        }
        chatHistory.addAssistantMessage(response.getResult());
        return ChatResponse.builder()
                .aiResponse(response.getResult())
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
