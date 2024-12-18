package com.example.demo.chat.client;

import com.example.demo.chat.config.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeploymentNameClient {
    private final OpenAiProperties openAiProperties;

    private final RestTemplate restTemplate;

    public List<String> fetchDeploymentNames() {
        String url = openAiProperties.getEndpoint() + openAiProperties.getDeploymentsListPath();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Api-Key", openAiProperties.getKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Response> response = restTemplate.exchange(url, HttpMethod.GET, entity, Response.class);

        return Optional.ofNullable(response.getBody())
                .map(Response::data)
                .stream()
                .flatMap(Collection::stream)
                .map(Response.DeploymentName::model)
                .collect(Collectors.toList());
    }

    record Response(List<DeploymentName> data) {

        record DeploymentName(String model) {
        }
    }
}
