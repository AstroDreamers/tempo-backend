//
//package com.andy.tempoapp.runner;
//
//import com.andy.tempoapp.dto.response.SensorsDto;
//import com.andy.tempoapp.entity.Alert;
//import com.andy.tempoapp.repository.AlertRepository;
//import com.andy.tempoapp.service.client.OpenAQRestClient;
//import com.andy.tempoapp.service.internal.AlertService;
//import com.andy.tempoapp.service.internal.EmailService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Objects;
//import java.util.Optional;
//
//@Component
//public class AlertStartupRunner implements ApplicationRunner {
//
//    private static final Logger log = LoggerFactory.getLogger(AlertStartupRunner.class);
//
//    private final AlertService alertService;
//    private final OpenAQRestClient openAQRestClient;
//    private final EmailService emailService;
//    private final AlertRepository alertRepository;
//
//
//    private String startupLocation = "2178";
//
//    private String startupSensorId = "3916";
//
//    public AlertStartupRunner(AlertService alertService, OpenAQRestClient openAQRestClient, EmailService emailService, AlertRepository alertRepository) {
//        this.alertService = alertService;
//        this.openAQRestClient = openAQRestClient;
//        this.emailService = emailService;
//        this.alertRepository = alertRepository;
//    }
//
//    @Override
//    public void run(ApplicationArguments args) {
//        log.info("Startup runner: triggering alert check");
//        try {
//            alertService.checkAndSendAlerts();
//            log.info("Alert check finished");
//        } catch (Exception e) {
//            log.error("Error while running alert check on startup", e);
//        }
//
//        // Print a single sensor value to console for manual verification
//        printSensorValue(startupLocation, startupSensorId);
//    }
//
//    private void printSensorValue(String locationId, String sensorId) {
//        try {
//            log.info("Fetching sensors for locationId={}", locationId);
//            SensorsDto sensorsDto = openAQRestClient.getSensorsByLocationId(locationId).join();
//
//            Double currentValue = null;
//            if (sensorsDto != null && sensorsDto.getResults() != null) {
//                currentValue = sensorsDto.getResults().stream()
//                        .filter(item -> String.valueOf(item.getId()).equals(sensorId))
//                        .findFirst()
//                        .map(item -> {
//                            Object v = item.getLatest() != null ? item.getLatest().getValue() : null;
//                            if (v == null) return null;
//                            if (v instanceof Number) return ((Number) v).doubleValue();
//                            try {
//                                return Double.valueOf(v.toString());
//                            } catch (NumberFormatException ex) {
//                                log.warn("Unable to parse sensor value to Double for sensorId={}: {}", sensorId, v);
//                                return null;
//                            }
//                        })
//                        .orElse(null);
//            }
//            // Option B: explicit null check with orElse
//            Long id = 2L;
//            Alert alert = alertRepository.findById(id).orElse(null);
//            if (alert != null && currentValue != null) {
//                emailService.sendAlertNotification(alert, currentValue);
//            } else {
//                log.debug("Alert or currentValue missing for id={}", id);
//            }
//
//            // direct console output for quick manual check
//            System.out.println(Objects.toString(currentValue, "null"));
//            log.info("Printed currentValue for location={} sensorId={} -> {}", locationId, sensorId, currentValue);
//        } catch (Exception e) {
//            log.error("Error fetching/printing sensor value for location={} sensorId={}", locationId, sensorId, e);
//        }
//    }
//}
