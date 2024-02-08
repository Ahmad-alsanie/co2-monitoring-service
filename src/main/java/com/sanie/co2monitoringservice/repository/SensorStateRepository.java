package com.sanie.co2monitoringservice.repository;

import java.util.List;
import java.util.UUID;

public interface SensorStateRepository {
    void addMeasurement(UUID sensorId, int co2Level);
    List<Integer> getMeasurements(UUID sensorId);
    void clearMeasurements(UUID sensorId);
}
