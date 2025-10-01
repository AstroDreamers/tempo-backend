package com.andy.tempoapp.service.client;

import com.andy.tempoapp.dto.request.AnalysisRequest;

import java.util.concurrent.CompletableFuture;

public interface OpenAIClient {

    CompletableFuture<String> askWithData(AnalysisRequest analysisRequestDto);

    CompletableFuture<String> conversation(String chat);
}
