package com.example.demo.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherData(Current current) {
    public record Current(
            @JsonProperty("temp_c")
            double temperature,
            Condition condition
    ) {
        public record Condition(String text) {
        }
    }
}
