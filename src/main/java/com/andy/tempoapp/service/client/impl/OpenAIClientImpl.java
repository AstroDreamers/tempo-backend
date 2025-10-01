package com.andy.tempoapp.service.client.impl;

import com.andy.tempoapp.dto.request.AnalysisRequest;
import com.andy.tempoapp.service.client.OpenAIClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OpenAIClientImpl implements OpenAIClient {
    private final ChatClient chatClient;

    public OpenAIClientImpl(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder.build();
    }





    @Async
    @Override
    @Cacheable(
            value = "analysisCache",
            key = "#analysisRequestDto.latitude + '_' + #analysisRequestDto.longitude + '_' + #analysisRequestDto.parameterName",
            unless = "#result == null"
    )
    public CompletableFuture<String> askWithData(AnalysisRequest analysisRequestDto) {
        if( analysisRequestDto.getJsonData().isEmpty()) {
            return  CompletableFuture.completedFuture("There is no data to ask!");
        }


        String basePrompt = "You are an expert in atmospheric science. Analyze the following time series data for the given location and parameter. Summarize the main trends, anomalies, and provide a short interpretation for a general audience. Highlight any periods of unusually high or low values, and suggest possible reasons if relevant.\n" +
                "\n" +
                "Location: %s (%.5f, %.5f)\n" +
                "Parameter: %s (%s)\n" +
                "Time Range: %s to %s\n" +
                "Data: %s\n" +
                "\n" +
                "Try to explain what these value mean to normal residents, will the air quality affect their health?" +
                "Your response should be about 3 sentences with bulletpoint, concise, clear, and suitable for non-experts.";
        // Format jsonData as a readable string for the prompt
        StringBuilder dataBuilder = new StringBuilder();
        for (AnalysisRequest.TimeSeriesData data : analysisRequestDto.getJsonData()) {
            dataBuilder.append(String.format("(%s, %.2e)\n", data.getTimestamp(), data.getValue()));
        }

        String prompt = String.format(
                basePrompt,
                analysisRequestDto.getLocationName(),
                analysisRequestDto.getLatitude(),
                analysisRequestDto.getLongitude(),
                analysisRequestDto.getParameterName(),
                analysisRequestDto.getUnits(),
                analysisRequestDto.getStartDate(),
                analysisRequestDto.getEndDate(),
                dataBuilder.toString()
        );

        String response = chatClient
                .prompt(prompt)
                .call()
                .content();

        return CompletableFuture.completedFuture(response);
    }


    @Async
    @Override
    @Cacheable(
            value = "conversationCache",
            key = "#chat.trim().toLowerCase()",
            unless = "#chat.length() > 50 || #result == null"
    )
    public CompletableFuture<String> conversation(String chat) {

        if (chat.length() > 500) {
            return CompletableFuture.completedFuture("The question is too long!");
        }

        if (chat == null ||  chat.equals("")) {
            return CompletableFuture.completedFuture("The question is empty!");
        }


        String basePrompt = "You are an expert in atmospheric science." +
                "Your job is to answer or discuss only about amotspheric science." +
                "For unrelated question, please say: Sorry, i do not know about this!" +
                "Your response should be 2-3 sentences, consise, clear and do not use too many jargons, but rather explain simply." +
                "Here is the user chat: ";


        String prompt  = basePrompt + chat;

        String response = chatClient
                .prompt(prompt)
                .call()
                .content();
        return CompletableFuture.completedFuture(response);
    }
}
