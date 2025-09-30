package com.andy.tempoapp.service.client;

import com.andy.tempoapp.dto.response.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OpenAQRestClient {
    // general info
    CompletableFuture<SingleLocationDto> getLocationByLocationId(String locationsId);
    CompletableFuture<SensorsDto> getSensorsByLocationId(String locationsId);

    // location info based on lat,lon, radius or bbox
    CompletableFuture<LocationFromPosDto> getLocationFromPosition(String coordinates, Integer radius, String bbox, int limit, int page);

    // latest measurement of a location
    CompletableFuture<LatestMeasureDto> getLatestMeasureByLocationId(String locationsId, int limit, int page, Instant dateTimeMin);


    // // get a sensor aggregated measurement from period

    // hourly
    CompletableFuture<MeasurementDto> getHourlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);


    // daily
    CompletableFuture<MeasurementDto> getDailyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);

    // monthly
    CompletableFuture<MeasurementDto> getMonthlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);

    // yearly
    CompletableFuture<MeasurementDto> getYearlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);

}
