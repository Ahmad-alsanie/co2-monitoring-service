package integration;

import com.sanie.co2monitoringservice.Co2MonitoringServiceApplication;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.service.MeasurementService;
import com.sanie.co2monitoringservice.service.SensorMetricsService;
import com.sanie.co2monitoringservice.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Co2MonitoringServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SensorMetricsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SensorMetricsService sensorMetricsService;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private SensorService sensorService;

    private UUID validSensorId;

    @BeforeEach
    public void setup(){
        validSensorId = UUID.randomUUID();
        sensorService.recordSensor(validSensorId, Status.OK);
        measurementService.recordMeasurement(validSensorId, 1400, LocalDateTime.now().minusDays(2));
        measurementService.recordMeasurement(validSensorId, 2100, LocalDateTime.now().minusDays(3));
        measurementService.recordMeasurement(validSensorId, 1700, LocalDateTime.now().minusDays(4));
    }

    @Test
    public void testGetAlertsForSensorWithAlerts() throws Exception {

        mockMvc.perform(get("/api/v1/sensors/" + validSensorId + "/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxLast30Days").value(2100.0))
                .andExpect(jsonPath("$.avgLast30Days").value(1733.3333333333333));
    }

    @Test
    public void testGetAlertsForSensorWithNoAlerts() throws Exception {
        UUID sensorWithNoMetrics = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/sensors" + sensorWithNoMetrics + "/metrics"))
                .andExpect(status().isNotFound());
    }
}
