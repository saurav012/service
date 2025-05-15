package com.weather.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "weather")
@Configuration
public class ServiceConfig {


}
