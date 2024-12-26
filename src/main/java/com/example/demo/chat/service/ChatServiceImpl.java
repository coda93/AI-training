package com.example.demo.chat.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.example.demo.chat.config.OpenAiProperties;
import com.example.demo.chat.dto.ChatParams;
import com.example.demo.chat.dto.ChatResponse;
import com.example.demo.chat.manger.ChatHistoryManager;
import com.example.demo.chat.manger.DeploymentModelManager;
import com.example.demo.chat.manger.PromptExecutionSettingsManager;
import com.example.demo.exception.AIResponseException;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatHistoryManager chatHistoryManager;
    private final PromptExecutionSettingsManager promptExecutionSettingsManager;
    private final DeploymentModelManager deploymentModelManager;
    private Kernel kernel;

    public ChatServiceImpl(OpenAiProperties openAiProperties,
                           DeploymentModelManager deploymentModelManager,
                           ChatHistoryManager chatHistoryManager,
                           PromptExecutionSettingsManager promptExecutionSettingsManager) {
        this.chatHistoryManager = chatHistoryManager;
        this.promptExecutionSettingsManager = promptExecutionSettingsManager;
        this.deploymentModelManager = deploymentModelManager;
        this.kernel = createKernel(openAiProperties);
    }

    @Override
    public ChatResponse getChatResponse(String prompt) {
        try {
            KernelFunction<String> chatFunction = getChatFunction();
            var response = kernel.invokeAsync(chatFunction)
                    .withArguments(getKernelFunctionArguments(prompt, chatHistoryManager.getChatHistory()))
                    .withPromptExecutionSettings(promptExecutionSettingsManager.getSettings())
                    .block();

            chatHistoryManager.addUserMessage(prompt);
            if (response == null) {
                throw new AIResponseException("There is no response from AI. Try with an another prompt");
            }
            String result = response.getResult();
            if (result == null || result.trim().isEmpty()) {
                throw new AIResponseException("There is no response from AI. Try with an another model");
            }
            chatHistoryManager.addAssistantMessage(result);

            return ChatResponse.builder()
                    .aiResponse(response.getResult())
                    .model(kernel.getService(ChatCompletionService.class).getModelId())
                    .build();
        } catch (Exception e) {
            throw new AIResponseException("There is no response from AI. Try with an another model");
        }
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
    public ChatResponse changeModel() {
        OpenAiProperties propertiesWithNewModel = deploymentModelManager.updateModel();
        this.kernel = createKernel(propertiesWithNewModel);
        return ChatResponse.builder()
                .aiResponse("You have just changed the model to --> " + propertiesWithNewModel.getDeploymentName())
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

    private Kernel createKernel(OpenAiProperties openAiProperties) {
        String clientOpenAIKey = openAiProperties.getKey();
        String clientOpenAIEndpoint = openAiProperties.getEndpoint();
        String clientOpenAIDeploymentName = openAiProperties.getDeploymentName();

        OpenAIAsyncClient openAIAsyncClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(clientOpenAIKey))
                .endpoint(clientOpenAIEndpoint)
                .buildAsyncClient();

        OpenAIChatCompletion chatCompletionService = OpenAIChatCompletion.builder()
                .withModelId(clientOpenAIDeploymentName)
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();

        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
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
