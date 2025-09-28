package com.andy.tempoapp.service.client;

import com.andy.tempoapp.dto.response.LatestMeasureDto;
import com.andy.tempoapp.dto.response.LocationDto;
import com.andy.tempoapp.dto.response.SensorsDto;

import java.time.Instant;

public interface OpenAQRestClient {
    LocationDto getLocationById(String locationsId);
    SensorsDto getSensorsByLocationId(String locationsId);

    LatestMeasureDto getLatestMeasureById(String locationsId);
    LatestMeasureDto getLatestMeasureById(String locationsId,  int limit, int page, Instant dateTimeMin);

}
