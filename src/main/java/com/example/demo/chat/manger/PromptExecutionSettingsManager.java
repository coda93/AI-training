package com.example.demo.chat.manger;

import com.example.demo.chat.dto.ChatParams;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PromptExecutionSettingsManager {

    private PromptExecutionSettings settings = PromptExecutionSettings.builder().build();

    public void updateSettings(ChatParams chatParams) {
        this.settings = PromptExecutionSettings.builder()
                .withTemperature(chatParams.temperature().orElse(settings.getTemperature()))
                .withTopP(chatParams.topP().orElse(settings.getTopP()))
                .withPresencePenalty(chatParams.presencePenalty().orElse(settings.getPresencePenalty()))
                .withFrequencyPenalty(chatParams.frequencyPenalty().orElse(settings.getFrequencyPenalty()))
                .withMaxTokens(chatParams.maxTokens().orElse(settings.getMaxTokens()))
                .withBestOf(chatParams.bestOf().orElse(settings.getBestOf()))
                .withStopSequences(chatParams.stopSequences().orElse(settings.getStopSequences()))
                .build();
    }

    public void resetPromptExecutionSettings() {
        settings = PromptExecutionSettings.builder().build();
    }

    public String getPromptExecutionSettingsString() {
        return "PromptExecutionSettings{" +
                "temperature=" + settings.getTemperature() +
                ", topP=" + settings.getTopP() +
                ", maxTokens=" + settings.getMaxTokens() +
                ", presencePenalty=" + settings.getPresencePenalty() +
                ", frequencyPenalty=" + settings.getFrequencyPenalty() +
                ", resultsPerPrompt=" + settings.getResultsPerPrompt() +
                ", bestOf=" + settings.getBestOf() +
                ", stopSequences=" + settings.getStopSequences() +
                '}';
    }
}
