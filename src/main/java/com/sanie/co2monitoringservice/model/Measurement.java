package com.sanie.co2monitoringservice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="MEASUREMENT")
public class Measurement {

    @Id
    @Column(name="MEASUREMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="CO2_LEVEL")
    private int co2;

    @Column(name = "TIME")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "SENSOR_ID")
    private Sensor sensor;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

}
