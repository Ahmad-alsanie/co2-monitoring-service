package com.sanie.co2monitoringservice.controller;

import com.sanie.co2monitoringservice.dto.SensorMetricsDTO;
import com.sanie.co2monitoringservice.service.SensorMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors")
public class SensorMetricsController {

    @Autowired
    private SensorMetricsService sensorMetricsService;

    @GetMapping("/{uuid}/metrics")
    public ResponseEntity<SensorMetricsDTO> getSensorMetrics(@PathVariable UUID uuid) {
        SensorMetricsDTO metrics = sensorMetricsService.calculateMetrics(uuid);
        return ResponseEntity.ok(metrics);
    }
}
