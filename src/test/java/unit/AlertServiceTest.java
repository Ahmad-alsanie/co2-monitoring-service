package unit;

import com.sanie.co2monitoringservice.configuration.SensorProperties;
import com.sanie.co2monitoringservice.dto.AlertDTO;
import com.sanie.co2monitoringservice.model.Alert;
import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.repository.AlertRepository;
import com.sanie.co2monitoringservice.repository.MeasurementRepository;
import com.sanie.co2monitoringservice.repository.SensorRepository;
import com.sanie.co2monitoringservice.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private SensorProperties sensorProperties;

    @InjectMocks
    private AlertService alertService;

    private List<Integer> alertLevelMeasurements;
    private List<Measurement> belowLevelMeasurements;

    private Sensor sensor;
    private UUID sensorId;

    private final static int THRESHOLD = 2000;
    private final static int TIMES = 3;

    @BeforeEach
    public void setup() {
        sensorId = UUID.randomUUID();
        sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setStatus(Status.ALERT);
        alertLevelMeasurements = Arrays.asList(2100, 2200, 2300);
        Measurement m1 = new Measurement(); m1.setCo2(1900);
        Measurement m2 = new Measurement(); m2.setCo2(1800);
        Measurement m3 = new Measurement(); m3.setCo2(1700);
        belowLevelMeasurements = Arrays.asList(m1, m2, m3);
    }


    @Test
    public void testCreateAlert() {

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));


        alertService.createAlert(sensorId, alertLevelMeasurements);

        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    public void testClearAlertIfConditionsMet() {
        SensorProperties.Thresholds thresholds = mock(SensorProperties.Thresholds.class);


        when(thresholds.getWarnLevel()).thenReturn(THRESHOLD);
        when(thresholds.getTimes()).thenReturn(TIMES);

        when(sensorProperties.getThresholds()).thenReturn(thresholds);

        when(sensorProperties.getThresholds().getWarnLevel()).thenReturn(THRESHOLD);
        when(sensorProperties.getThresholds().getTimes()).thenReturn(TIMES);

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        when(measurementRepository.findTop3BySensorOrderByTimeDesc(sensor)).thenReturn(belowLevelMeasurements);

        alertService.clearAlertIfConditionsMet(sensorId);

        ArgumentCaptor<Sensor> sensorCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepository).save(sensorCaptor.capture());

        assertEquals(Status.OK, sensorCaptor.getValue().getStatus());
    }


    @Test
    public void testFindAlertsForSensor() {
        UUID sensorId = UUID.randomUUID();
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);

        Alert alert = new Alert();
        alert.setSensor(sensor);

        when(alertRepository.findBySensorId(sensorId)).thenReturn(Arrays.asList(alert));

        List<AlertDTO> result = alertService.findAlertsForSensor(sensorId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(sensorId, result.get(0).getSensorId());
    }

}

