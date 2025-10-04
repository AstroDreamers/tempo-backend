package com.andy.tempoapp.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "alert_enabled", nullable = false)
    private boolean alertEnabled;



    public Subscription() {
    }

    public Subscription(User user, String locationName, boolean alertEnabled) {
        this.user = user;
        this.locationName = locationName;
        this.alertEnabled = alertEnabled;
    }
}