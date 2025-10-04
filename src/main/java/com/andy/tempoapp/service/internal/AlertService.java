package com.andy.tempoapp.service.internal;

import com.andy.tempoapp.dto.request.AlertRequest;
import com.andy.tempoapp.dto.response.AlertResponse;
import com.andy.tempoapp.entity.Alert;
import com.andy.tempoapp.entity.Subscription;
import com.andy.tempoapp.mapper.DtoMapper;
import com.andy.tempoapp.repository.AlertRepository;
import com.andy.tempoapp.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DtoMapper mapper;

    public AlertService(
            AlertRepository alertRepository,
            SubscriptionRepository subscriptionRepository,
            DtoMapper mapper) {
        this.alertRepository = alertRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.mapper = mapper;
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
}
