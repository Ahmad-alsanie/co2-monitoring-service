package unit;

import com.sanie.co2monitoringservice.dto.SensorMetricsDTO;
import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.repository.MeasurementRepository;
import com.sanie.co2monitoringservice.service.SensorMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SensorMetricsServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    private SensorMetricsService sensorMetricsService;

    private UUID sensorId = UUID.randomUUID();

    private List<Measurement> mockMeasurements;

    @BeforeEach
    public void setup() {
        Sensor sensor = new Sensor();
        Measurement m1 = new Measurement();
        Measurement m2 = new Measurement();
        Measurement m3 = new Measurement();
        Measurement m4 = new Measurement();
        sensor.setId(sensorId);
        m1.setSensor(sensor);
        m1.setCo2(2001);
        m1.setTime(LocalDateTime.now().minusDays(1));
        m2.setSensor(sensor);
        m2.setCo2(1500);
        m2.setTime(LocalDateTime.now().minusDays(2));
        m3.setSensor(sensor);
        m3.setCo2(1700);
        m3.setTime(LocalDateTime.now().minusDays(5));
        m4.setSensor(sensor);
        m4.setCo2(2500);
        m4.setTime(LocalDateTime.now().minusDays(10));
        mockMeasurements = Arrays.asList(
                m1,m2,m3,m4
        );
    }

    @Test
    public void calculateMetricsReturnsCorrectValues() {

        when(measurementRepository.findBySensorIdAndTimeAfter(eq(sensorId), any(LocalDateTime.class)))
                .thenReturn(mockMeasurements);

        SensorMetricsDTO result = sensorMetricsService.calculateMetrics(sensorId);

        assertEquals(2500, result.getMaxLast30Days());
        assertEquals(1925.25, result.getAvgLast30Days());
    }
}

