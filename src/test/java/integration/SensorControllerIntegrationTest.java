package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanie.co2monitoringservice.Co2MonitoringServiceApplication;
import com.sanie.co2monitoringservice.dto.MeasurementDTO;
import com.sanie.co2monitoringservice.dto.SensorDTO;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Co2MonitoringServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SensorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SensorService sensorService;

    private UUID expectedUUID;
    private SensorDTO sensorDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(){
        expectedUUID = UUID.randomUUID();
        objectMapper = new ObjectMapper();
        sensorDTO = new SensorDTO();
        sensorDTO.setStatus("OK");
        sensorDTO.setSensorId(expectedUUID);
    }

    @Test
    public void testRecordSensor() throws Exception {
        String measurementJson = objectMapper.writeValueAsString(sensorDTO);
        mockMvc.perform(post("/api/v1/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(measurementJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSensorFound() throws Exception {
        String measurementJson = objectMapper.writeValueAsString(sensorDTO);
        mockMvc.perform(post("/api/v1/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(measurementJson))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/sensors/" +sensorDTO.getSensorId() +"/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testSensorNotFound() throws Exception {
        UUID invalidUUID = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/sensors/" +invalidUUID +"/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}