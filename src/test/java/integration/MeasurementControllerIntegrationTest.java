package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanie.co2monitoringservice.Co2MonitoringServiceApplication;
import com.sanie.co2monitoringservice.dto.MeasurementDTO;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.repository.SensorRepository;
import com.sanie.co2monitoringservice.service.SensorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = Co2MonitoringServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MeasurementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SensorService sensorService;

    private MeasurementDTO measurementDTO;

    private UUID expectedUUID;

    @BeforeEach
    public void setup(){
        expectedUUID = UUID.randomUUID();
        sensorService.recordSensor(expectedUUID, Status.OK);
        measurementDTO = new MeasurementDTO();
        measurementDTO.setSensorId(expectedUUID);
        measurementDTO.setCo2Level(2000);
    }


    @Test
    public void testRecordMeasurementWhenValidRecordIsPassed() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        String measurementJson = objectMapper.writeValueAsString(measurementDTO);
        mockMvc.perform(post("/api/v1/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(measurementJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testRecordMeasurementWhenSensorNotFound() throws Exception {
        UUID invalidUUID = UUID.randomUUID();
        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurementDTO.setSensorId(invalidUUID);
        measurementDTO.setCo2Level(2000);

        ObjectMapper objectMapper = new ObjectMapper();
        String measurementJson = objectMapper.writeValueAsString(measurementDTO);

        mockMvc.perform(post("/api/v1/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(measurementJson))
                .andExpect(status().isNotFound());
    }
}

