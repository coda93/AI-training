package com.example.demo.chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "client-openai")
@Getter
@Setter
public class OpenAiProperties {

    private String key;
    private String endpoint;
    private String deploymentsListPath;
    private String deploymentName;
}
