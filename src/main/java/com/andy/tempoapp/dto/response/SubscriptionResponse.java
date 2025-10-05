package com.andy.tempoapp.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionResponse {
    private Long id;
    private String locationId;
    private String locationName;
    private Double lat;
    private Double lon;
    private List<AlertResponse> alerts;
}