package com.example.demo.weather;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "weather-api")
@Getter
@Setter
public class WeatherApiProperties {
    private String key;
    private String endpoint;
}
