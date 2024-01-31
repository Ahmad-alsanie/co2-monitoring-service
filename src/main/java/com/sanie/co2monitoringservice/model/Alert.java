package com.sanie.co2monitoringservice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="ALERT")
public class Alert {
    @Id
    @Column(name="ALERT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="START_TIME")
    private LocalDateTime startTime;
    @Column(name="END_TIME")
    private LocalDateTime endTime;
    @Column(name = "MEASUREMENT_SUMMARY")
    private String summary;


    @ManyToOne
    @JoinColumn(name = "SENSOR_ID")
    private Sensor sensor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
