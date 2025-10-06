package com.andy.tempoapp.dto.request;


import lombok.Data;

@Data
public class HistoricalData {
    private String datetime;
    private double pm25;
    // getters and setters
}