package com.andy.tempoapp.service.client.impl;

import com.andy.tempoapp.dto.response.*;
import com.andy.tempoapp.service.client.OpenAQRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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


    @Override
    public LocationFromPosDto getLocationFromPosition(String coordinates, Integer radius, String bbox, int limit, int page) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/v3/locations")
                            .queryParam("iso", "US")
                            .queryParam("limit", limit)
                            .queryParam("page", page);
                    if (bbox != null) {
                        builder.queryParam("bbox", bbox);
                    } else if (coordinates != null && radius != null) {
                        builder.queryParam("radius", radius);
                        builder.queryParam("coordinates", coordinates);
                    }

                    return builder.build();
                })
                .retrieve()
                .body(LocationFromPosDto.class);
    }


    public SingleLocationDto getLocationByLocationId(String locationsId) {
        return restClient.get()
                .uri("/v3/locations/{locationsId}",  locationsId)
                .retrieve()
                .body(SingleLocationDto.class);
    }

    @Override
    public SensorsDto getSensorsByLocationId(String locationsId) {
        return restClient.get()
                .uri("/v3/locations/{locations_id}/sensors", locationsId)
                .retrieve()
                .body(SensorsDto.class);
    }



    @Override
    public LatestMeasureDto getLatestMeasureByLocationId(String locationsId, int limit, int page, Instant dateTimeMin) {
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

    @Override
    public MeasurementDto getHourlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/v3/sensors/{sensors_id}/hours")
                            .queryParam("limit", limit)
                            .queryParam("page", page)
                            .queryParam("datetime_from", dateTimeFrom.toString())
                            .queryParam("datetime_to", dateTimeTo.toString());
                    return builder.build(sensorsId);
                })
                .retrieve()
                .body(MeasurementDto.class);
    }

    @Override
    public MeasurementDto getDailyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/v3/sensors/{sensors_id}/hours/daily")
                            .queryParam("limit", limit)
                            .queryParam("page", page)
                            .queryParam("datetime_from", dateTimeFrom.toString())
                            .queryParam("datetime_to", dateTimeTo.toString());
                    return builder.build(sensorsId);
                })
                .retrieve()
                .body(MeasurementDto.class);
    }

    @Override
    public MeasurementDto getMonthlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/v3/sensors/{sensors_id}/hours/monthly")
                            .queryParam("limit", limit)
                            .queryParam("page", page)
                            .queryParam("datetime_from", dateTimeFrom.toString())
                            .queryParam("datetime_to", dateTimeTo.toString());
                    return builder.build(sensorsId);
                })
                .retrieve()
                .body(MeasurementDto.class);    }

    @Override
    public MeasurementDto getYearlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/v3/sensors/{sensors_id}/hours/yearly")
                            .queryParam("limit", limit)
                            .queryParam("page", page)
                            .queryParam("datetime_from", dateTimeFrom.toString())
                            .queryParam("datetime_to", dateTimeTo.toString());
                    return builder.build(sensorsId);
                })
                .retrieve()
                .body(MeasurementDto.class);     }
}
