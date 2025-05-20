package com.weather.service.controller;

import com.weather.service.model.ClimateDto;
import com.weather.service.service.ClimateService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@RestController
@RequestMapping("/v1/weather")
public class ClimateController {

    private final ClimateService climateService;

    public ClimateController(ClimateService climateService) {
        this.climateService = climateService;
    }

    @GetMapping
    public Mono<ClimateDto> getWeatherInfo(@NonNull @RequestParam(value = "city") String city,
                                           @RequestHeader(value = "cacheControl", required = false)String cacheControl) {
        return climateService.getClimateDetails(city,cacheControl);

    }
}

