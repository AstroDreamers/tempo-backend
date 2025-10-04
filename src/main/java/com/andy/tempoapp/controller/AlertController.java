package com.andy.tempoapp.controller;

import com.andy.tempoapp.dto.request.AlertRequest;
import com.andy.tempoapp.dto.response.AlertResponse;
import com.andy.tempoapp.entity.User;
import com.andy.tempoapp.service.internal.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions/{locationId}/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @AuthenticationPrincipal User user,
            @PathVariable String locationId) {
        List<AlertResponse> alerts =
                alertService.getAlertsForSubscription(user.getId(), locationId);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping
    public ResponseEntity<AlertResponse> configureAlert(
            @AuthenticationPrincipal User user,
            @PathVariable String locationId,
            @Valid @RequestBody AlertRequest request) {
        AlertResponse alert =
                alertService.configureAlert(user.getId(), locationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @PatchMapping("/{sensorId}/enable")
    public ResponseEntity<AlertResponse> enableAlert(
            @AuthenticationPrincipal User user,
            @PathVariable String locationId,
            @PathVariable String sensorId) {
        AlertResponse alert =
                alertService.enableAlert(user.getId(), locationId, sensorId);
        return ResponseEntity.ok(alert);
    }

    @PatchMapping("/{sensorId}/disable")
    public ResponseEntity<AlertResponse> disableAlert(
            @AuthenticationPrincipal User user,
            @PathVariable String locationId,
            @PathVariable String sensorId) {
        AlertResponse alert =
                alertService.disableAlert(user.getId(), locationId, sensorId);
        return ResponseEntity.ok(alert);
    }

    @DeleteMapping("/{sensorId}")
    public ResponseEntity<?> deleteAlert(
            @AuthenticationPrincipal User user,
            @PathVariable String locationId,
            @PathVariable String sensorId) {
        alertService.deleteAlert(user.getId(), locationId, sensorId);
        return ResponseEntity.ok().body("Alert deleted!");
    }
}