package com.example.demo.chat.plugin;

import com.example.demo.weather.WeatherClient;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherPlugin {

    private final WeatherClient weatherClient;

    @DefineKernelFunction(
            name = "temperature",
            description = "Get the current temperature in Celsius for a city")
    public String temperature(
            @KernelFunctionParameter(
                    name = "city",
                    description = "City to get the temperature for",
                    required = true)
            String city) {

        double temperature = fetchCurrentTemperature(city);
        return String.format("%.1f°C", temperature);
    }

    @DefineKernelFunction(
            name = "conditions",
            description = "Get the current weather conditions for a city")
    public String conditions(
            @KernelFunctionParameter(
                    name = "city",
                    description = "City to get the weather conditions for",
                    required = true)
            String city) {

        return fetchWeatherConditions(city);
    }

    private double fetchCurrentTemperature(String city) {
        return weatherClient.fetchWeatherData(city).current().temperature();
    }

    private String fetchWeatherConditions(String city) {
        return weatherClient.fetchWeatherData(city).current().condition().text();
    }
}
