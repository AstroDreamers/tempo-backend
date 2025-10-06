package com.andy.tempoapp.dto.request;

import lombok.Data;

import java.util.List;



@Data
public class PredictionRequest {
    private List<HistoricalData> historical_data;
    // getters and setters
}