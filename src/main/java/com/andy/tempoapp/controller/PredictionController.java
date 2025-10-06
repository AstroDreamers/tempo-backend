package com.andy.tempoapp.controller;



import com.andy.tempoapp.dto.request.HistoricalData;
import com.andy.tempoapp.dto.request.PredictionResponse;
import com.andy.tempoapp.service.client.impl.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PredictionController {

    private final PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict-pm25")
    public PredictionResponse predict(@RequestBody List<HistoricalData> historicalData) {
        return predictionService.getPrediction(historicalData);
    }
}
