package com.andy.tempoapp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class AlertRequest {
    @NotNull
    private String sensorId;

    private boolean alertEnabled;

    private Double threshold;

    private LocalTime quietStart;

    private LocalTime quietEnd;
}