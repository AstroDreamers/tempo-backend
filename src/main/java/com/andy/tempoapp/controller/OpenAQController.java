
package com.andy.tempoapp.controller;

import com.andy.tempoapp.dto.response.*;
import com.andy.tempoapp.service.client.OpenAQRestClient;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("oa")
public class OpenAQController {

    private OpenAQRestClient openAQRestClient;

    @GetMapping("/locations")
    public CompletableFuture<LocationFromPosDto> getLocationFromPos(
            @RequestParam(required = false) String coordinates,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String bbox,
            @RequestParam int limit,
            @RequestParam int page
    ){
        return openAQRestClient.getLocationFromPosition(coordinates, radius, bbox, limit, page);
    }

    @GetMapping("/locations/{locationsId}")
    public CompletableFuture<SingleLocationDto> getLocationById(@PathVariable("locationsId") String locationsId) {
        return openAQRestClient.getLocationByLocationId(locationsId);
    }

    @GetMapping("/locations/{locationsId}/sensors")
    public CompletableFuture<SensorsDto> getSensorsByLocationId(@PathVariable("locationsId") String locationsId) {
        return openAQRestClient.getSensorsByLocationId(locationsId);
    }

    @GetMapping("/locations/{locationsId}/latest")
    public CompletableFuture<LatestMeasureDto> getLatestMeasureById(
            @PathVariable("locationsId") String locationsId,
            @RequestParam(value = "limit", required = true) @Min(1) Integer limit,
            @RequestParam(value = "page", required = true) @Min(1) Integer page,
            @RequestParam(value = "datetime_min", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeMin
    ) {
        return openAQRestClient.getLatestMeasureByLocationId(locationsId, limit, page, dateTimeMin);
    }

    @GetMapping("/sensors/{sensorId}/hours")
    public CompletableFuture<MeasurementDto> getHourlyMeasurementBySensorId(
            @PathVariable("sensorId") String sensorId,
            @RequestParam(value = "datetime_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeFrom,
            @RequestParam(value = "datetime_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeTo,
            @RequestParam(value = "limit", required = true) @Min(1) int limit,
            @RequestParam(value = "page", required = true) @Min(1) int page
    ){
        return openAQRestClient.getHourlyMeasurementBySensorId(sensorId, dateTimeFrom, dateTimeTo, limit, page);
    }

    @GetMapping("/sensors/{sensorId}/hours/daily")
    public CompletableFuture<MeasurementDto> getDailyMeasurementBySensorId(
            @PathVariable("sensorId") String sensorId,
            @RequestParam(value = "datetime_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeFrom,
            @RequestParam(value = "datetime_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeTo,
            @RequestParam(value = "limit", required = true) @Min(1) int limit,
            @RequestParam(value = "page", required = true) @Min(1) int page
    ){
        return openAQRestClient.getDailyMeasurementBySensorId(sensorId, dateTimeFrom, dateTimeTo, limit, page);
    }

    @GetMapping("/sensors/{sensorId}/hours/monthly")
    public CompletableFuture<MeasurementDto> getMonthlyMeasurementBySensorId(
            @PathVariable("sensorId") String sensorId,
            @RequestParam(value = "datetime_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeFrom,
            @RequestParam(value = "datetime_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeTo,
            @RequestParam(value = "limit", required = true) @Min(1) int limit,
            @RequestParam(value = "page", required = true) @Min(1) int page
    ){
        return openAQRestClient.getMonthlyMeasurementBySensorId(sensorId, dateTimeFrom, dateTimeTo, limit, page);
    }

    @GetMapping("/sensors/{sensorId}/hours/yearly")
    public CompletableFuture<MeasurementDto> getYearlyMeasurementBySensorId(
            @PathVariable("sensorId") String sensorId,
            @RequestParam(value = "datetime_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeFrom,
            @RequestParam(value = "datetime_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTimeTo,
            @RequestParam(value = "limit", required = true) @Min(1) int limit,
            @RequestParam(value = "page", required = true) @Min(1) int page
    ){
        return openAQRestClient.getYearlyMeasurementBySensorId(sensorId, dateTimeFrom, dateTimeTo, limit, page);
    }
}
