package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanie.co2monitoringservice.Co2MonitoringServiceApplication;
import com.sanie.co2monitoringservice.dto.SensorDTO;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.service.AlertService;
import com.sanie.co2monitoringservice.service.MeasurementService;
import com.sanie.co2monitoringservice.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Co2MonitoringServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AlertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private MeasurementService measurementService;

    private UUID validSensorId;

    @BeforeEach
    public void setup(){
        validSensorId = UUID.randomUUID();
        sensorService.recordSensor(validSensorId, Status.OK);
        //add 3 consecutive measurement to trigger alert
        measurementService.recordMeasurement(validSensorId, 2001, LocalDateTime.now());
        measurementService.recordMeasurement(validSensorId, 2002, LocalDateTime.now());
        measurementService.recordMeasurement(validSensorId, 2003, LocalDateTime.now());
    }

    @Test
    public void testGetAlertsForSensorWithAlerts() throws Exception {

        mockMvc.perform(get("/api/v1/sensors/" + validSensorId + "/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetAlertsForSensorWithNoAlerts() throws Exception {
        UUID sensorWithNoAlerts = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/sensors/" + sensorWithNoAlerts + "/alerts"))
                .andExpect(status().isNoContent());
    }
}
