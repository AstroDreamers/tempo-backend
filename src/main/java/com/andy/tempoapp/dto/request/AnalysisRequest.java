// src/main/java/com/andy/tempoapp/dto/request/AnalysisRequestDto.java
package com.andy.tempoapp.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisRequest {
    private String locationName;
    private double latitude;
    private double longitude;
    private String parameterName;
    private String units;
    private String startDate;
    private String endDate;
    private List<TimeSeriesData> jsonData;

    @Data
    public static class TimeSeriesData {
        private String timestamp;
        private double value;
    }
}
