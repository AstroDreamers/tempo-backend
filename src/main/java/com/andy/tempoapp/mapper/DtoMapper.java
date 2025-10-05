package com.andy.tempoapp.mapper;

import com.andy.tempoapp.dto.response.AlertResponse;
import com.andy.tempoapp.dto.response.SubscriptionResponse;
import com.andy.tempoapp.dto.response.UserResponse;
import com.andy.tempoapp.entity.Alert;
import com.andy.tempoapp.entity.Subscription;
import com.andy.tempoapp.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    // User to UserResponse
    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getDisplayUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        return response;
    }

    // Subscription to SubscriptionResponse
    public SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        response.setLocationId(subscription.getLocationId());
        response.setLocationName(subscription.getLocationName());
        response.setLat(subscription.getLat());
        response.setLon(subscription.getLon());

        // Map alerts if loaded
        if (subscription.getAlerts() != null) {
            response.setAlerts(
                    subscription.getAlerts().stream()
                            .map(this::toAlertResponse)
                            .collect(Collectors.toList())
            );
        }
        return response;
    }

    // Alert to AlertResponse
    public AlertResponse toAlertResponse(Alert alert) {
        AlertResponse response = new AlertResponse();
        response.setId(alert.getId());
        response.setSensorId(alert.getSensorId());
        response.setAlertEnabled(alert.isAlertEnabled());
        response.setThreshold(alert.getThreshold());
        response.setQuietStart(alert.getQuietStart());
        response.setQuietEnd(alert.getQuietEnd());
        return response;
    }

    // List converters
    public List<SubscriptionResponse> toSubscriptionResponseList(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(this::toSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public List<AlertResponse> toAlertResponseList(List<Alert> alerts) {
        return alerts.stream()
                .map(this::toAlertResponse)
                .collect(Collectors.toList());
    }
}