package com.weather.service.servicetest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.weather.model.WeatherResponse;
import com.weather.service.config.RedisConfiguration;
import com.weather.service.config.WebConfig;
import com.weather.service.mapper.ServiceMapper;
import com.weather.service.model.ClimateDto;
import com.weather.service.service.ClimateService;
import com.weather.service.service.RedisCacheImplementation;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {


    private ObjectMapper objectMapper;
    public ClimateService climateService;
    public WebClient webClient;
    @Mock
    public WebConfig webConfig;
    @Mock
    RedisCacheImplementation redisCacheImplementation;
    @Mock
    RedisConfiguration redisConfiguration;
    private MockWebServer mockWebServer;

    ServiceMapper serviceMapper;
    private final ClimateDto dummyResponse = new ClimateDto(10, 32);

    @BeforeEach
    void setup() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        serviceMapper = new ServiceMapper();
        webClient = WebClient.builder().build();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString()).build();
        climateService = new ClimateService(webConfig, serviceMapper, webClient, webClient, redisCacheImplementation, redisConfiguration);
    }

    @Test
    void shouldCallPrimaryService_whenCacheControlIsNull() throws IOException {
        String city = "Sydney";
        String cacheControl = null;
        String weatherResponse = objectMapper.writeValueAsString(objectMapper.readValue(new URL("file:src/test/java/com/weather/service/helper/weather_api_successful_response.json"), WeatherResponse.class));
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/current?access_key=0184e2645db8285770821779f1412e7f&query=Sydney")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(weatherResponse)
                            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        StepVerifier.create(climateService.getClimateDetails(city, cacheControl))
                .assertNext(value-> {
                    Assertions.assertEquals(value.temperature_degrees(),18);
                })
                .verifyComplete();

    }

    @Test
    void shouldCallSecondryService_when_primaryService_fails_whenCacheControlIsNull() throws IOException {
        String city = "Sydney";
        String cacheControl = null;
        String weatherResponse = objectMapper.writeValueAsString(objectMapper.readValue(new URL("file:src/test/java/com/weather/service/helper/climateDto_success.json"), ClimateDto.class));
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/data/2.5/weather?access_key=0184e2645db8285770821779f1412e7f&query=Sydney")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(weatherResponse)
                            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        StepVerifier.create(climateService.getClimateDetails(city, cacheControl))
                .assertNext(value-> {
                    Assertions.assertEquals(value.temperature_degrees(),28);
                })
                .verifyComplete();

    }

}
