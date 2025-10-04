package com.andy.tempoapp.dto.response;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AlertResponse {
    private Long id;
    private String sensorId;
    private boolean alertEnabled;
    private Double threshold;
    private LocalTime quietStart;
    private LocalTime quietEnd;
}