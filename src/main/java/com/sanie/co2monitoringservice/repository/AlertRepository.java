package com.sanie.co2monitoringservice.repository;

import com.sanie.co2monitoringservice.model.Alert;
import com.sanie.co2monitoringservice.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findBySensorOrderByStartTimeDesc(Sensor sensor);
    List<Alert> findBySensorId(UUID sensorId);
}
