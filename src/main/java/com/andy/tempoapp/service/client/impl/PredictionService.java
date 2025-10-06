package com.andy.tempoapp.service.client.impl;

import com.andy.tempoapp.dto.request.HistoricalData;
import com.andy.tempoapp.dto.request.PredictionRequest;
import com.andy.tempoapp.dto.request.PredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

@Service
public class PredictionService {
    @Value("${prediction.service.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public PredictionResponse getPrediction(List<HistoricalData> historicalData) {
        if (historicalData.size() < 24) {
            throw new IllegalArgumentException("At least 24 hours of historical data required");
        }
        PredictionRequest request = new PredictionRequest();
        request.setHistorical_data(historicalData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PredictionRequest> entity = new HttpEntity<>(request, headers);

        String url = baseUrl + "/predict";
        ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(
                url,
                entity,
                PredictionResponse.class
        );
        return response.getBody();
    }
}
