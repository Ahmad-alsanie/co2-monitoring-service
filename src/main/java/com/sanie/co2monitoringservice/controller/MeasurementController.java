package com.sanie.co2monitoringservice.controller;

import com.sanie.co2monitoringservice.dto.MeasurementDTO;
import com.sanie.co2monitoringservice.service.MeasurementService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementController {

    @Autowired
    private MeasurementService measurementService;

    private final static Logger LOGGER = LoggerFactory.getLogger(MeasurementController.class);

    @PostMapping
    public ResponseEntity<Void> recordMeasurement(@RequestBody MeasurementDTO measurementDTO) {
        try {
            measurementService.recordMeasurement(measurementDTO.getSensorId(), measurementDTO.getCo2Level(), LocalDateTime.now());
        } catch (EntityNotFoundException e) {
            LOGGER.error("Sensor for the given measurement not found: {}", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
