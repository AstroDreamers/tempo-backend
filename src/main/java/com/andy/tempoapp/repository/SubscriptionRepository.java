package com.andy.tempoapp.repository;


import com.andy.tempoapp.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT s FROM Subscription s " +
            "LEFT JOIN FETCH s.alerts " +
            "WHERE s.user.id = :userId")
    List<Subscription> findByUserIdWithAlerts(@Param("userId") Long userId);

    boolean existsByUserIdAndLocationId(Long userId, String locationId);  // ✅ boolean, Long userId

    Optional<Subscription> findByUserIdAndLocationId(Long userId, String locationId);  // Useful for getting specific subscription
}
