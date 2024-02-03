package com.sanie.co2monitoringservice.repository;

import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findTop3BySensorOrderByTimeDesc(Sensor sensor);
    List<Measurement> findBySensorIdAndTimeAfter(UUID sensorId, LocalDateTime dateAfter);
}
