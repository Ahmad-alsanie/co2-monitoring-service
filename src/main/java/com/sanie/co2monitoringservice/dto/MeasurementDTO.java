package com.sanie.co2monitoringservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class MeasurementDTO {
    private UUID sensorId;
    private int co2Level;
    private LocalDateTime time;

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public int getCo2Level() {
        return co2Level;
    }

    public void setCo2Level(int co2Level) {
        this.co2Level = co2Level;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
