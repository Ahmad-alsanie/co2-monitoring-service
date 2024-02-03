package com.sanie.co2monitoringservice.service;

import com.sanie.co2monitoringservice.dto.SensorMetricsDTO;
import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SensorMetricsService {

    @Autowired
    private MeasurementRepository measurementRepository;

    public SensorMetricsDTO calculateMetrics(UUID sensorId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Measurement> measurements = measurementRepository.findBySensorIdAndTimeAfter(sensorId, thirtyDaysAgo);

        double average = measurements.stream()
                .mapToInt(Measurement::getCo2)
                .average()
                .orElse(0.0);

        double max = measurements.stream()
                .mapToInt(Measurement::getCo2)
                .max()
                .orElse(0);

        return new SensorMetricsDTO(max, average);
    }
}

