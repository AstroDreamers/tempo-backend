package com.andy.tempoapp.controller;


import com.andy.tempoapp.dto.response.LatestMeasureDto;
import com.andy.tempoapp.dto.response.LocationDto;
import com.andy.tempoapp.dto.response.SensorsDto;
import com.andy.tempoapp.service.client.OpenAQRestClient;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("oa")
public class OpenAQController {

    private OpenAQRestClient openAQRestClient;


    @GetMapping("/locations/{locationsId}")
    public LocationDto getLocationById(@PathVariable("locationsId") String locationsId) {
        return openAQRestClient.getLocationById(locationsId);
    }

    @GetMapping("/locations/{locationsId}/sensors")
    public SensorsDto getSensorsByLocationId(@PathVariable("locationsId") String locationsId) {
        return openAQRestClient.getSensorsByLocationId(locationsId);
    }

    @GetMapping("/locations/{locationsId}/latest")
    public LatestMeasureDto getLatestMeasureById(
            @PathVariable("locationsId") String locationsId,
            @RequestParam(value = "limit", required = false) @Min(1) Integer limit,
            @RequestParam(value = "page", required = false) @Min(1) Integer page,
            @RequestParam(value = "datetime_min", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeMin
            ) {
        if (limit != null || page != null || dateTimeMin != null) {
            return openAQRestClient.getLatestMeasureById(locationsId, limit, page, dateTimeMin);

        }

        return openAQRestClient.getLatestMeasureById(locationsId);
    }


}
