package com.sanie.co2monitoringservice.controller;

import com.sanie.co2monitoringservice.dto.SensorDTO;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @GetMapping("/{sensorId}/status")
    public ResponseEntity<Status> getSensorStatus(@PathVariable UUID sensorId) {
        Status status = sensorService.findSensorById(sensorId)
                .map(Sensor::getStatus)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found"));
        return ResponseEntity.ok(status);
    }

    @PostMapping
    public ResponseEntity<Void> recordSensor(@RequestBody SensorDTO sensorDTO) {
        Status status = Status.valueOf(sensorDTO.getStatus().toUpperCase());
        sensorService.recordSensor(sensorDTO.getSensorId(), status);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
