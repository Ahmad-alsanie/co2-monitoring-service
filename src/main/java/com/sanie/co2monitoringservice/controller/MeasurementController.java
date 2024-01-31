package com.sanie.co2monitoringservice.controller;

import com.sanie.co2monitoringservice.dto.MeasurementDTO;
import com.sanie.co2monitoringservice.service.MeasurementService;
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

    @PostMapping
    public ResponseEntity<Void> recordMeasurement(@RequestBody MeasurementDTO measurementDTO) {
        measurementService.recordMeasurement(measurementDTO.getSensorId(), measurementDTO.getCo2Level(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
