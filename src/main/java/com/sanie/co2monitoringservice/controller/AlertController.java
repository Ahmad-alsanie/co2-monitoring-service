package com.sanie.co2monitoringservice.controller;

import com.sanie.co2monitoringservice.dto.AlertDTO;
import com.sanie.co2monitoringservice.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping("/{sensorId}")
    public ResponseEntity<List<AlertDTO>> getAlertsForSensor(@PathVariable UUID sensorId) {
        List<AlertDTO> alerts = alertService.findAlertsForSensor(sensorId);
        return ResponseEntity.ok(alerts);
    }
}
