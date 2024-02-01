package com.sanie.co2monitoringservice.controller;

import com.sanie.co2monitoringservice.dto.AlertDTO;
import com.sanie.co2monitoringservice.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors")
public class AlertController {

    @Autowired
    private AlertService alertService;

    private final static Logger LOGGER = LoggerFactory.getLogger(AlertController.class);

    @GetMapping("/{sensorId}/alerts")
    public ResponseEntity<List<AlertDTO>> getAlertsForSensor(@PathVariable UUID sensorId) {
        List<AlertDTO> alerts = alertService.findAlertsForSensor(sensorId);
        if(alerts.size() == 0){
            LOGGER.info("No alerts fo sensor id {}", sensorId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(alerts);
    }
}
