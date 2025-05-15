package com.weather.service.service;

import com.weather.model.WeatherResponse;
import com.weather.service.config.ApplicationProperties;
import com.weather.service.config.WebConfig;
import com.weather.service.mapper.ServiceMapper;
import com.weather.service.model.ClimateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;

@Service
public class ClimateService {
    Logger logger = LoggerFactory.getLogger(ClimateService.class);
    private final ServiceMapper serviceMapper;
    public final WebConfig webConfig;
    private final WebClient weatherstackClient;
    private final WebClient openWeatherMapClient;


    public ClimateService(WebConfig webConfig, ServiceMapper serviceMapper, WebClient weatherstackClient, WebClient openWeatherMapClient) {
        this.weatherstackClient = weatherstackClient;
        this.openWeatherMapClient = openWeatherMapClient;
        this.webConfig = webConfig;
        this.serviceMapper = serviceMapper;
    }

    public Mono<ClimateDto> getClimateDetails(String city) {
        return serviceCallOne(city)
                .map(serviceMapper::generatingFinalResponse)
                .onErrorResume(error -> {
                    logger.info("Primary provider failed: " + error.getMessage());
                    return serviceCalltwo(city);
                }).timeout(Duration.ofSeconds(10));
    }

    /**
     * creating query parameter for service call
     *
     * @param city
     * @return
     */
    private MultiValueMap<String, String> queryParameter(String city) {
        MultiValueMap<String, String> queryParameter = new LinkedMultiValueMap<>();
        queryParameter.add(ApplicationProperties.ACCESS_KEY, "0184e2645db8285770821779f1412e7f");
        queryParameter.add(ApplicationProperties.QUERY, city);
        return queryParameter;

    }

    /**
     * Method will retrieve response from wheather api
     *
     * @param city
     * @return
     */
    public Mono<WeatherResponse> serviceCallOne(String city) {
        return weatherstackClient.get()
                .uri(uriBuilder -> uriBuilder.path("/current").queryParams(queryParameter(city)).build())
                .retrieve()
                .bodyToMono(WeatherResponse.class).log()
                .doOnError(value -> Mono.error(RuntimeException::new))
                .elapsed()
                .doOnNext(tuple -> logger.info("Total time taken to Get Weather Call By TimeTaken::{}", tuple.getT1() + "ms"))
                .map(Tuple2::getT2)
                .doOnError(httpStatus -> {
                    logger.info("Failed to get data fromWeather API, Error::{}", httpStatus.getMessage());
                    throw new RuntimeException();
                })
                .doOnNext(weatherapi -> logger.info("Response received from Service one call for Get Weather deatils API::{}", weatherapi));


    }

    /**
     * Method will retrevide from source two
     *
     * @param city
     * @return
     */
    public Mono<ClimateDto> serviceCalltwo(String city) {
        return openWeatherMapClient.get()
                .uri(uriBuilder -> uriBuilder.path("/data/2.5/weather").queryParams(queryParameter(city)).build())
                .retrieve()
                .bodyToMono(ClimateDto.class)
                .doOnError(value -> Mono.error(RuntimeException::new))
                .elapsed()
                .doOnNext(tuple -> logger.info("Total time taken to Get Weather Call By TimeTaken::{}", tuple.getT1() + "ms"))
                .map(Tuple2::getT2)
                .doOnError(httpStatus -> {
                    logger.info("Failed to get User Favourites from Weather API, Error::{}", httpStatus.getMessage());
                    throw new RuntimeException();
                })
                .doOnNext(weatherapi -> logger.info("Response received from Weather API::{}", weatherapi));


    }

}


