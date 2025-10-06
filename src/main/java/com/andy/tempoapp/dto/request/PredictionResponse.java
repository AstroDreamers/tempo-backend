package com.andy.tempoapp.dto.request;

import lombok.Data;

import java.util.List;


@Data
public class PredictionResponse {
    private boolean success;
    private List<Prediction> predictions;
    private String forecast_start;
    // getters and setters
}