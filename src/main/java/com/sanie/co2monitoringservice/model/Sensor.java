package com.sanie.co2monitoringservice.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="SENSOR")
public class Sensor {

    @Id
    @Column(name = "SENSOR_ID")
    // @GeneratedValue(strategy = GenerationType.AUTO) for now I want to allow users to set the uuid manually for sensors using sensor API
    private UUID id;

    @Column(name="STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
