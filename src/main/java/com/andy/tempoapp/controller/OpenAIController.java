package com.andy.tempoapp.controller;


import com.andy.tempoapp.dto.request.AnalysisRequestDto;
import com.andy.tempoapp.dto.response.AnalysisResponseDto;
import com.andy.tempoapp.service.client.OpenAIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ai")
public class OpenAIController {
    private OpenAIClient openAIClient;

    @Autowired
    public OpenAIController(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    @PostMapping("/ask-with-data")
    public AnalysisResponseDto askWithData(@RequestBody AnalysisRequestDto analysisRequestDto) {
       CompletableFuture<String> response = openAIClient.askWithData(analysisRequestDto);
        AnalysisResponseDto analysisResponseDto = new AnalysisResponseDto();
        analysisResponseDto.setResponse(response.join());
        return analysisResponseDto;
    }
}
