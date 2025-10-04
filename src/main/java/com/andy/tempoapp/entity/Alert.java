package com.andy.tempoapp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    private String sensorId;

    private boolean alertEnabled = false;

    private LocalTime quietStart;

    private LocalTime quietEnd;

    private Double threshold;
}
