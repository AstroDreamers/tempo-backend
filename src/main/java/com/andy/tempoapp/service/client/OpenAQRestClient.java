package com.andy.tempoapp.service.client;

import com.andy.tempoapp.dto.response.*;

import java.time.Instant;
import java.util.List;

public interface OpenAQRestClient {
    // general info
    SingleLocationDto getLocationByLocationId(String locationsId);
    SensorsDto getSensorsByLocationId(String locationsId);

    // location info based on lat,lon, radius or bbox
    LocationFromPosDto getLocationFromPosition(String coordinates, Integer radius, String bbox, int limit, int page);

    // latest measurement of a location
    LatestMeasureDto getLatestMeasureByLocationId(String locationsId, int limit, int page, Instant dateTimeMin);


    // // get a sensor aggregated measurement from period

    // hourly
    MeasurementDto getHourlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);


    // daily
    MeasurementDto getDailyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);

    // monthly
    MeasurementDto getMonthlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);

    // yearly
    MeasurementDto getYearlyMeasurementBySensorId(String sensorsId, Instant dateTimeFrom, Instant dateTimeTo, int limit, int page);

}
