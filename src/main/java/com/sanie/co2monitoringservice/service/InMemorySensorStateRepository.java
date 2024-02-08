package com.sanie.co2monitoringservice.service;

import com.sanie.co2monitoringservice.repository.SensorStateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;

@Service
public class InMemorySensorStateRepository implements SensorStateRepository {

    private final ConcurrentHashMap<UUID, LinkedList<Integer>> sensorMeasurements = new ConcurrentHashMap<>();

    @Override
    public synchronized void addMeasurement(UUID sensorId, int co2Level) {
        LinkedList<Integer> measurements = sensorMeasurements.computeIfAbsent(sensorId, k -> new LinkedList<>());
        measurements.add(co2Level);
        int consecutiveThreshold = 3;
        while (measurements.size() > consecutiveThreshold) {
            measurements.removeFirst();
        }
    }

    @Override
    public List<Integer> getMeasurements(UUID sensorId) {
        return sensorMeasurements.getOrDefault(sensorId, new LinkedList<>());
    }

    @Override
    public void clearMeasurements(UUID sensorId) {
        sensorMeasurements.remove(sensorId);
    }
}

