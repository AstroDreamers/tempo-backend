package com.andy.tempoapp.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SubscriptionRequest {
    @NotNull
    private String locationId;

    @NotNull
    private Double lat;

    @NotNull
    private String locationName;

    @NotNull
    private Double lon;
}
