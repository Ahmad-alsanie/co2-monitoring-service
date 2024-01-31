package com.sanie.co2monitoringservice.service;

import com.sanie.co2monitoringservice.configuration.SensorProperties;
import com.sanie.co2monitoringservice.dto.AlertDTO;
import com.sanie.co2monitoringservice.model.Alert;
import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.repository.AlertRepository;
import com.sanie.co2monitoringservice.repository.MeasurementRepository;
import com.sanie.co2monitoringservice.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private SensorProperties sensorProperties;

    /**
     * Create an alert when the sensor status changes to ALERT.
     *
     * @param sensorId The UUID of the sensor.
     * @param measurements The list of consecutive measurements triggering the alert.
     */
    public void createAlert(UUID sensorId, List<Integer> measurements) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalStateException("Sensor not found with ID: " + sensorId));

        Alert alert = new Alert();
        alert.setSensor(sensor);
        alert.setStartTime(LocalDateTime.now());
        alert.setEndTime(LocalDateTime.now());
        alert.setSummary("Triggered by measurements: " + measurements);
        alertRepository.save(alert);
    }

    /**
     * Method to check and potentially clear the ALERT status based on consecutive low measurements.
     *
     * @param sensorId The UUID of the sensor.
     */
    public void clearAlertIfConditionsMet(UUID sensorId) {
        List<Measurement> latestMeasurements = measurementRepository.findTop3BySensorOrderByTimeDesc(sensorRepository.findById(sensorId).orElseThrow(() -> new IllegalStateException("Sensor not found")));

        // Check if all three latest measurements are below 2000 ppm
        int warnLevel = sensorProperties.getThresholds().getWarnLevel();
        int consecutiveThreshold = sensorProperties.getThresholds().getTimes();
        boolean allBelowThreshold = latestMeasurements.size() == consecutiveThreshold && latestMeasurements.stream().allMatch(measurement -> measurement.getCo2() < warnLevel);

        if (allBelowThreshold) {
            Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new IllegalStateException("Sensor not found"));
            // Assuming an ALERT status exists and is now being cleared to OK
            if (sensor.getStatus() == Status.ALERT) {
                sensor.setStatus(Status.OK);
                sensorRepository.save(sensor);
            }
        }
    }

    public List<AlertDTO> findAlertsForSensor(UUID sensorId) {
        List<Alert> alerts = alertRepository.findBySensorId(sensorId);
        return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private AlertDTO convertToDTO(Alert alert) {
        AlertDTO dto = new AlertDTO();
        dto.setSensorId(alert.getSensor().getId());
        dto.setStartTime(alert.getStartTime());
        dto.setEndTime(alert.getEndTime());
        dto.setAlertMessage(alert.getSummary());
        return dto;
    }
}
