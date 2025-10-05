package com.andy.tempoapp.service.client.impl;

import com.andy.tempoapp.dto.request.AnalysisRequest;
import com.andy.tempoapp.service.client.OpenAIClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class OpenAIClientImpl implements OpenAIClient {
    private final ChatClient chatClient;

    public OpenAIClientImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Async
    @Override
    @Cacheable(
        value = "analysisCache",
        key = "T(java.util.Objects).hash(#req.locationName, #req.latitude, #req.longitude, #req.parameterName, #req.startDate, #req.endDate, #req.overallIndex)",
        unless = "#result == null"
    )
    public CompletableFuture<String> askWithData(AnalysisRequest req) {
        // 1) Guard clauses
        if (req == null) {
            return CompletableFuture.completedFuture("No request payload.");
        }
        if (req.getJsonData() == null || req.getJsonData().isEmpty()) {
            // allow insights from summary-only payload (overallIndex + perPollutant)
            if (req.getOverallIndex() == null && (req.getPerPollutant() == null || req.getPerPollutant().isEmpty())) {
                return CompletableFuture.completedFuture("There is no data to analyze.");
            }
        }

        // 2) Build a compact JSON-like summary string (model-friendly, but small)
        StringBuilder series = new StringBuilder();
        if (req.getJsonData() != null) {
            // keep small to avoid token blow-ups
            int limit = Math.min(50, req.getJsonData().size());
            for (int i = 0; i < limit; i++) {
                var d = req.getJsonData().get(i);
                series.append("(")
                      .append(d.getTimestamp()).append(", ")
                      .append(String.format("%.3f", d.getValue()))
                      .append(")\n");
            }
            if (req.getJsonData().size() > limit) {
                series.append("… ").append(req.getJsonData().size() - limit).append(" more points\n");
            }
        }

        String perPollutant = "";
        if (req.getPerPollutant() != null && !req.getPerPollutant().isEmpty()) {
            var sb = new StringBuilder();
            req.getPerPollutant().forEach(p ->
                sb.append(p.getKey()).append("=")
                  .append(String.format("%.3f", p.getValue()))
                  .append(" ").append(Objects.toString(p.getUnits(), ""))
                  .append(" (subIndex=").append(String.format("%.2f", p.getSubIndex()))
                  .append(")\n")
            );
            perPollutant = sb.toString();
        }

        String overall = req.getOverallIndex() != null
                ? String.format("%.2f", req.getOverallIndex())
                : "n/a";

        String dominant = req.getDominant() != null ? req.getDominant().toUpperCase() : "n/a";

        String prevLine = "";
        if (req.getPrevious() != null) {
            var y = req.getPrevious().getYesterdaySameHour() != null ? req.getPrevious().getYesterdaySameHour().getOverallIndex() : null;
            var a = req.getPrevious().getAvg24h() != null ? req.getPrevious().getAvg24h().getOverallIndex() : null;
            prevLine = String.format("Prev: yesterdaySameHour=%s, avg24h=%s",
                    y != null ? String.format("%.2f", y) : "n/a",
                    a != null ? String.format("%.2f", a) : "n/a");
        }

        // 3) System & user messages (concise, resident-friendly)
        String system = """
                You are an atmospheric-science assistant for residents.
                Be concise and concrete. Use 3–5 bullet points max.
                Tone: calm, practical. Avoid jargon; explain briefly when needed.
                If uncertainty is high (few sensors), say so in one short line.
                """;

        String user = """
                Location: %s (%.5f, %.5f)
                Window: %s → %s (%s)
                Overall AQI (1–5): %s (dominant=%s)
                Available pollutants: %s
                Per-pollutant (latest): 
                %s
                %s
                Breakpoints (AQI 1..5 category ranges): %s
                Time series sample (trimmed):
                %s

                Output format (markdown):
                - One line summarizing overall category and driver.
                - 2–3 bullets on what it means health-wise for general public & sensitive groups.
                - 1–2 actionable suggestions (e.g., schedule, mask, ventilation).
                - If yesterday/24h trend suggests improvement or worsening, mention it.
                """.formatted(
                Objects.toString(req.getLocationName(), "Unknown"),
                req.getLatitude(), req.getLongitude(),
                Objects.toString(req.getStartDate(), ""), Objects.toString(req.getEndDate(), ""),
                Objects.toString(req.getTimezone(), ""),
                overall, dominant,
                Objects.toString(req.getAvailable(), "n/a"),
                perPollutant.isBlank() ? "n/a" : perPollutant,
                prevLine,
                Objects.toString(req.getReferenceCompact(), "see app table"),
                series.toString().isBlank() ? "(omitted)" : series.toString()
        );

        try {
            String response = chatClient
                    .prompt()
                    .system(system)
                    .user(user)
                    .call()
                    .content();

            if (response == null || response.isBlank()) {
                return CompletableFuture.completedFuture("No AI response available right now.");
            }
            return CompletableFuture.completedFuture(response.trim());
        } catch (Exception ex) {
            // Log full stack on server; return safe string to client
            // log.error("askWithData failed", ex);
            return CompletableFuture.completedFuture("Unable to generate AI insight at this time.");
        }
    }

    @Async
    @Override
    @Cacheable(
        value = "conversationCache",
        key = "#chat == null ? '' : #chat.trim().toLowerCase()",
        unless = "#chat == null || #chat.length() > 50 || #result == null"
    )
    public CompletableFuture<String> conversation(String chat) {
        if (chat == null || chat.isBlank()) {
            return CompletableFuture.completedFuture("The question is empty!");
        }
        if (chat.length() > 500) {
            return CompletableFuture.completedFuture("The question is too long!");
        }

        String system = """
                You are an atmospheric-science assistant. Only answer questions about air quality, weather, or atmospheric chemistry. 
                If unrelated, say: "Sorry, I do not know about this."
                Keep answers to 2–3 short sentences, simple vocabulary.
                """;
        String user = chat;

        try {
            String response = chatClient
                    .prompt()
                    .system(system)
                    .user(user)
                    .call()
                    .content();
            return CompletableFuture.completedFuture(
                    (response == null || response.isBlank()) ? "No response right now." : response.trim()
            );
        } catch (Exception ex) {
            // log.error("conversation failed", ex);
            return CompletableFuture.completedFuture("Sorry, I cannot answer right now.");
        }
    }
}