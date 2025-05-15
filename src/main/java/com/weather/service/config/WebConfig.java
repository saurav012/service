package com.weather.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {
    @Bean(name = "weatherstackClient")
    public WebClient webClient() {
        return WebClient.builder().baseUrl("http://api.weatherstack.com").build();
    }

    @Bean(name = "openWeatherMapClient")
    public WebClient webclientOne() {
        return WebClient.builder().baseUrl("http://api.openweathermap.org").build();
    }
}
