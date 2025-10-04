package com.andy.tempoapp.controller;



import com.andy.tempoapp.dto.request.SubscriptionRequest;
import com.andy.tempoapp.dto.response.SubscriptionResponse;
import com.andy.tempoapp.entity.User;
import com.andy.tempoapp.service.internal.SubscriptionService;
import com.andy.tempoapp.service.internal.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptions(@AuthenticationPrincipal User user) {
        List<SubscriptionResponse> subscriptionResponses = subscriptionService.getUserSubscriptions(user.getId());

        return ResponseEntity.ok().body(subscriptionResponses);
    }


    @GetMapping("/{locationId}")
    public ResponseEntity<SubscriptionResponse> getSingleSubscription(@AuthenticationPrincipal User user, @PathVariable String locationId) {
        SubscriptionResponse subscriptionResponse = subscriptionService.getSubscription(user.getId(), locationId);
        return ResponseEntity.ok().body(subscriptionResponse);
    }


    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(
            @AuthenticationPrincipal User user,
            @RequestBody SubscriptionRequest subscriptionRequest) {

        SubscriptionResponse subscription = subscriptionService.subscribe(
                user.getId(),
                subscriptionRequest.getLocationId(),
                subscriptionRequest.getLat(),
                subscriptionRequest.getLon()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<?> unsubscribe(
            @AuthenticationPrincipal User user,
            @PathVariable String locationId) {

        subscriptionService.unsubscribe(user.getId(), locationId);
        return ResponseEntity.ok().body("Unsubscribed!");
    }


}
