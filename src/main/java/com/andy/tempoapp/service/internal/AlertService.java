package com.andy.tempoapp.service.internal;

import com.andy.tempoapp.dto.request.AlertRequest;
import com.andy.tempoapp.dto.response.AlertResponse;
import com.andy.tempoapp.dto.response.SensorsDto;
import com.andy.tempoapp.entity.Alert;
import com.andy.tempoapp.entity.Subscription;
import com.andy.tempoapp.mapper.DtoMapper;
import com.andy.tempoapp.repository.AlertRepository;
import com.andy.tempoapp.repository.SubscriptionRepository;
import com.andy.tempoapp.service.client.OpenAQRestClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DtoMapper mapper;
    private final OpenAQRestClient openAQRestClient;
    private final EmailService emailService;

    public AlertService(
            AlertRepository alertRepository,
            SubscriptionRepository subscriptionRepository,
            DtoMapper mapper,
            OpenAQRestClient openAQRestClient,
            EmailService emailService) {
        this.alertRepository = alertRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.mapper = mapper;
        this.openAQRestClient = openAQRestClient;
        this.emailService = emailService;
    }

    @Transactional
    public AlertResponse configureAlert(Long userId, String locationId, AlertRequest request) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndLocationId(userId, locationId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        Optional<Alert> existingAlert = alertRepository
                .findBySubscriptionIdAndSensorId(subscription.getId(), request.getSensorId());

        Alert alert;
        if (existingAlert.isPresent()) {
            alert = existingAlert.get();
        } else {
            alert = new Alert();
            alert.setSubscription(subscription);
            alert.setSensorId(request.getSensorId());
        }

        alert.setAlertEnabled(request.isAlertEnabled());
        alert.setThreshold(request.getThreshold());
        alert.setQuietStart(request.getQuietStart());
        alert.setQuietEnd(request.getQuietEnd());

        Alert saved = alertRepository.save(alert);
        return mapper.toAlertResponse(saved);
    }

    @Transactional
    public AlertResponse enableAlert(Long userId, String locationId, String sensorId) {
        Alert alert = getAlert(userId, locationId, sensorId);
        alert.setAlertEnabled(true);
        Alert saved = alertRepository.save(alert);
        return mapper.toAlertResponse(saved);
    }

    @Transactional
    public AlertResponse disableAlert(Long userId, String locationId, String sensorId) {
        Alert alert = getAlert(userId, locationId, sensorId);
        alert.setAlertEnabled(false);
        Alert saved = alertRepository.save(alert);
        return mapper.toAlertResponse(saved);
    }

    public List<AlertResponse> getAlertsForSubscription(Long userId, String locationId) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndLocationId(userId, locationId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        List<Alert> alerts = alertRepository.findBySubscriptionId(subscription.getId());
        return mapper.toAlertResponseList(alerts);
    }

    @Transactional
    public void deleteAlert(Long userId, String locationId, String sensorId) {
        Alert alert = getAlert(userId, locationId, sensorId);
        alertRepository.delete(alert);
    }

    private Alert getAlert(Long userId, String locationId, String sensorId) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndLocationId(userId, locationId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        return alertRepository
                .findBySubscriptionIdAndSensorId(subscription.getId(), sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found"));
    }

    /**
     * Scheduled job that runs every 15 minutes to check alerts
     * Cron: minute, hour, day, month, day-of-week
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional(readOnly = true)
    public void checkAndSendAlerts() {
        log.info("Start checking for alerts");

        List<Alert> enabledAlerts  = alertRepository.findByAlertEnabledTrue();
        log.info("Found {} enabled alerts", enabledAlerts.size());

        LocalTime now = LocalTime.now();

        for (Alert alert : enabledAlerts) {
            try {
                processAlert(alert, now);
            } catch (Exception e) {
                log.error("Error processing alert id={}: {}", alert.getId(), e.getMessage(), e);
            }
        }
        log.info("Alert check job completed");
    }


    private void processAlert(Alert alert, LocalTime currentTime) {
        if (isInQuietHours(currentTime, alert.getQuietStart(), alert.getQuietEnd())) {
            log.debug("Alert id={} is in quiet hours, skipping", alert.getId());
            return;
        }

        try {
            // Blockingly get the sensors response
            SensorsDto sensorsDto = openAQRestClient
                    .getSensorsByLocationId(alert.getSubscription().getLocationId())
                    .join(); // or .get() with timeout handling

            Double currentValue = null;
            if (sensorsDto != null && sensorsDto.getResults() != null) {
                currentValue = sensorsDto.getResults().stream()
                        .filter(item -> String.valueOf(item.getId()).equals(alert.getSensorId()))
                        .findFirst()
                        .map(item -> {
                            Object v = item.getLatest() != null ? item.getLatest().getValue() : null;
                            if (v == null) return null;
                            if (v instanceof Number) return ((Number) v).doubleValue();
                            try {
                                return Double.valueOf(v.toString());
                            } catch (NumberFormatException ex) {
                                log.warn("Unable to parse sensor value to Double for sensorId={}: {}", alert.getSensorId(), v);
                                return null;
                            }
                        })
                        .orElse(null);
            }

            if (currentValue == null) {
                log.debug("No current value for alert id={} sensorId={}", alert.getId(), alert.getSensorId());
                return;
            }

            Double threshold = alert.getThreshold();
            if (threshold != null && currentValue > threshold) {
                log.info("Alert triggered id={} sensorId={} currentValue={} threshold={}",
                        alert.getId(), alert.getSensorId(), currentValue, threshold);
                // TODO: send notification / persist event
                emailService.sendAlertNotification(alert, currentValue);

            } else {
                log.debug("Alert not triggered id={} currentValue={} threshold={}",
                        alert.getId(), currentValue, threshold);
            }
        } catch (Exception e) {
            log.error("Failed to fetch/process sensor value for alert id={} sensorId={}: {}",
                    alert.getId(), alert.getSensorId(), e.getMessage(), e);
        }
    }


    private boolean isInQuietHours(LocalTime current, LocalTime quietStart, LocalTime quietEnd) {
        if (quietStart == null || quietEnd == null) {
            return false;
        }

        // Handle quiet hours that span midnight
        if (quietStart.isBefore(quietEnd)) {
            // Normal case: e.g., 22:00 to 08:00 next day
            return !current.isBefore(quietStart) && !current.isAfter(quietEnd);
        } else {
            // Spans midnight: e.g., 22:00 to 08:00
            return !current.isBefore(quietStart) || !current.isAfter(quietEnd);
        }
    }




}
