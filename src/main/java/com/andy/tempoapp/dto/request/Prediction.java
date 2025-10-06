package com.andy.tempoapp.dto.request;

import lombok.Data;

@Data
public class Prediction {
    private String datetime;
    private double predicted_pm25;
    private int forecast_hour;
    // getters and setters
}
