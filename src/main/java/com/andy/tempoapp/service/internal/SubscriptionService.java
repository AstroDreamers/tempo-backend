package com.andy.tempoapp.service.internal;

import com.andy.tempoapp.dto.response.SubscriptionResponse;
import com.andy.tempoapp.entity.Subscription;
import com.andy.tempoapp.entity.User;
import com.andy.tempoapp.mapper.DtoMapper;
import com.andy.tempoapp.repository.SubscriptionRepository;
import com.andy.tempoapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            DtoMapper mapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public SubscriptionResponse subscribe(Long userId, String locationId, String locationName, Double lat, Double lon) {
        if (subscriptionRepository.existsByUserIdAndLocationId(userId, locationId)) {
            throw new IllegalArgumentException("Already subscribed to this location");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setLocationId(locationId);
        subscription.setLocationName(locationName);
        subscription.setLat(lat);
        subscription.setLon(lon);

        Subscription saved = subscriptionRepository.save(subscription);
        return mapper.toSubscriptionResponse(saved);
    }

    @Transactional
    public void unsubscribe(Long userId, String locationId) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndLocationId(userId, locationId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        subscription.getUser().getSubscriptions().remove(subscription);
        subscription.setUser(null);
        subscriptionRepository.delete(subscription);
    }

    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdWithAlerts(userId);
        return mapper.toSubscriptionResponseList(subscriptions);
    }

    public SubscriptionResponse getSubscription(Long userId, String locationId) {
        Subscription subscription = subscriptionRepository
                .findByUserIdAndLocationId(userId, locationId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        return mapper.toSubscriptionResponse(subscription);
    }
}