package com.andy.tempoapp.service.client.impl;

import com.andy.tempoapp.dto.response.LatestMeasureDto;
import com.andy.tempoapp.dto.response.LocationDto;
import com.andy.tempoapp.dto.response.SensorsDto;
import com.andy.tempoapp.service.client.OpenAQRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.time.Instant;


@Service
public class OpenAQRestClientImpl implements OpenAQRestClient {

    private final RestClient restClient;

    public OpenAQRestClientImpl(RestClient.Builder builder,
                                @Value("${api.openaq.url}") String baseUrl,
                                @Value("${api.openaq.key}") String apiKey
                                ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                        .build();
    }

    public LocationDto getLocationById(String locationsId) {
        return restClient.get()
                .uri("/v3/locations/{locationsId}",  locationsId)
                .retrieve()
                .body(LocationDto.class);
    }

    @Override
    public SensorsDto getSensorsByLocationId(String locationsId) {
        return restClient.get()
                .uri("/v3/locations/{locations_id}/sensors", locationsId)
                .retrieve()
                .body(SensorsDto.class);
    }




    // Default version
    @Override
    public LatestMeasureDto getLatestMeasureById(String locationsId) {
        return restClient.get()
                .uri("/v3/locations/{locations_id}/latest", locationsId)
                .retrieve()
                .body(LatestMeasureDto.class);
    }

    @Override
    public LatestMeasureDto getLatestMeasureById(String locationsId, int limit, int page, Instant dateTimeMin) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/v3/locations/{locations_id}/latest")
                            .queryParam("limit", limit)
                            .queryParam("page", page);
                    if (dateTimeMin != null) {
                        builder.queryParam("datetime_min", dateTimeMin.toString());
                    }
                    return builder.build(locationsId);
                })
                .retrieve()
                .body(LatestMeasureDto.class);
    }
}
