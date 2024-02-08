package com.sanie.co2monitoringservice.service;

import com.sanie.co2monitoringservice.configuration.SensorProperties;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.repository.SensorRepository;
import com.sanie.co2monitoringservice.repository.SensorStateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private AlertService alertService;

    @Autowired
    private SensorProperties sensorProperties;

    @Autowired
    private SensorStateRepository sensorStateRepository;

    /**
     * Updates sensor status and handles Alerts
     *
     * @param sensorId The UUID of the sensor.
     * @param co2Level The current co2 level
     */
    @Transactional
    public synchronized void updateSensorStatusAndHandleAlerts(UUID sensorId, int co2Level) {
        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new EntityNotFoundException("Sensor not found"));
        sensorStateRepository.addMeasurement(sensorId, co2Level);
        List<Integer> measurements = sensorStateRepository.getMeasurements(sensorId);

        updateStatus(sensor, measurements);
    }

    /**
     * Adds a sensor.
     *
     * @param sensorId The UUID of the sensor.
     * @param status The Sensor status - OK, WARN, ALERT).
     */
    @Transactional
    public void recordSensor(UUID sensorId, Status status){
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setStatus(status);
        sensorRepository.save(sensor);
    }

    /**
     * Returns a sensor for the passed id.
     *
     * @param id The UUID of the sensor.
     */
    public Optional<Sensor> findSensorById(UUID id) {
        return sensorRepository.findById(id);
    }

    public void updateStatus(Sensor sensor, List<Integer> measurements) {
        Status newStatus = determineStatus(measurements, sensor.getStatus());
        if (sensor.getStatus() != newStatus) {
            sensor.setStatus(newStatus);
            sensorRepository.save(sensor);

            if (newStatus == Status.ALERT) {
                alertService.createAlert(sensor.getId(), measurements);
            }
        }
    }

    public Status determineStatus(List<Integer> measurements, Status previousStatus) {
        int warnLevel = sensorProperties.getThresholds().getWarnLevel();
        int consecutiveThreshold = sensorProperties.getThresholds().getTimes();
        long aboveThreshold = measurements.stream().filter(co2 -> co2 > warnLevel).count();
        if (aboveThreshold >= consecutiveThreshold) {
            return Status.ALERT;
        } else if (measurements.get(measurements.size()-1) > warnLevel && previousStatus != Status.ALERT) {
            return Status.WARN;
        } else if (previousStatus != Status.ALERT){
            return Status.OK;
        }
        return Status.ALERT;
    }
}

