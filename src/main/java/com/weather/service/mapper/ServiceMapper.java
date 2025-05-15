package com.weather.service.mapper;

import com.weather.model.WeatherResponse;
import com.weather.service.model.ClimateDto;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    public ClimateDto generatingFinalResponse(WeatherResponse weatherResponse) {
        return new ClimateDto(0, weatherResponse.getCurrent().getTemperature());
    }


}
