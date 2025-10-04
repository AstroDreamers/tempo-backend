package com.andy.tempoapp.repository;

import com.andy.tempoapp.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Optional<Alert> findBySubscriptionIdAndSensorId(Long subscriptionId, String sensorId);

    List<Alert> findBySubscriptionId(Long subscriptionId);

    void deleteBySubscriptionIdAndSensorId(Long subscriptionId, String sensorId);
}