package com.example.demo.chat.manger;

import com.example.demo.chat.client.DeploymentNameClient;
import com.example.demo.chat.config.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DeploymentModelManager {

    private final OpenAiProperties openAiProperties;

    private final DeploymentNameClient deploymentNameClient;

    public OpenAiProperties updateModel() {
        List<String> deploymentNames = deploymentNameClient.fetchDeploymentNames();
        Random random = new Random();
        int randomIndex = random.nextInt(deploymentNames.size());
        String randomDeploymentName = deploymentNames.get(randomIndex);
        openAiProperties.setDeploymentName(randomDeploymentName);
        return openAiProperties;
    }
}
