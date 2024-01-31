package com.sanie.co2monitoringservice.dto;

import java.util.UUID;

public class SensorDTO {

    private UUID sensorId;

    private String status;

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
