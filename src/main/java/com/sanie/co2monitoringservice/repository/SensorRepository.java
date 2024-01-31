package com.sanie.co2monitoringservice.repository;

import com.sanie.co2monitoringservice.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {
}
