package com.example.demo.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class WeatherClient {

    private final WeatherApiProperties weatherApiProperties;

    private final RestTemplate restTemplate;

    public WeatherData fetchWeatherData(String city) {
        String url = UriComponentsBuilder.fromUriString(weatherApiProperties.getEndpoint())
                .queryParam("key", weatherApiProperties.getKey())
                .queryParam("q", city)
                .toUriString();
        return restTemplate.getForObject(url, WeatherData.class);
    }
}
