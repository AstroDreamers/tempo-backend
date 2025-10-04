package com.andy.tempoapp.controller;


import com.andy.tempoapp.dto.request.AnalysisRequest;
import com.andy.tempoapp.dto.request.ChatRequestDto;
import com.andy.tempoapp.dto.response.AIResponseDto;
import com.andy.tempoapp.service.client.OpenAIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/ai")
public class OpenAIController {
    private OpenAIClient openAIClient;

    @Autowired
    public OpenAIController(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    @PostMapping("/ask-with-data")
    public AIResponseDto askWithData(@RequestBody AnalysisRequest analysisRequestDto) {
        CompletableFuture<String> response = openAIClient.askWithData(analysisRequestDto);
        AIResponseDto analysisResponseDto = new AIResponseDto();
        analysisResponseDto.setResponse(response.join());
        return analysisResponseDto;
    }

    @PostMapping("/chat")
    public AIResponseDto chat(@RequestBody ChatRequestDto chatRequestDto) {
        CompletableFuture<String> response = openAIClient.conversation(chatRequestDto.getMessage());
        AIResponseDto chatResponseDto = new AIResponseDto();
        chatResponseDto.setResponse(response.join());
        return chatResponseDto;

    }
}
