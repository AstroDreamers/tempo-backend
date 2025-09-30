package com.andy.tempoapp.service.client;

import com.andy.tempoapp.dto.request.AnalysisRequestDto;

import java.util.concurrent.CompletableFuture;

public interface OpenAIClient {

    CompletableFuture<String> askWithData(AnalysisRequestDto analysisRequestDto);
}
