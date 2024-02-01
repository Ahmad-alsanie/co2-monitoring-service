package com.sanie.co2monitoringservice.service;

import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.repository.MeasurementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MeasurementService {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private AlertService alertService;

    /**
     * Records a measurement.
     *
     * @param sensorId The UUID of the sensor.
     * @param co2 The list of consecutive measurements triggering the alert.
     * @param time timestamp of measurement
     */
    @Transactional
    public void recordMeasurement(UUID sensorId, int co2, LocalDateTime time) {
        // Assuming a method in SensorService to fetch Sensor by ID | we can add sensors using our sensor API
        Sensor sensor = sensorService.findSensorById(sensorId)
                .orElseThrow(() -> new EntityNotFoundException("Sensor not found with ID: " + sensorId));

        Measurement measurement = new Measurement();
        measurement.setSensor(sensor); // Set the sensor directly
        measurement.setCo2(co2);
        measurement.setTime(time);
        measurementRepository.save(measurement);

        // Then call to update the sensor's status
        sensorService.updateSensorStatusAndHandleAlerts(sensorId, co2);

        // check if conditions are met to clear an alert | We can replace this by scheduled method to run periodically
        alertService.clearAlertIfConditionsMet(sensorId);
    }
}

