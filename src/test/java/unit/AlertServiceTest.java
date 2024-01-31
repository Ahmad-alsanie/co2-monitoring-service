package unit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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


    @Test
    public void testCreateAlert() {
        UUID sensorId = UUID.randomUUID();
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        List<Integer> measurements = Arrays.asList(2100, 2200, 2300);
        alertService.createAlert(sensorId, measurements);

        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    public void testClearAlertIfConditionsMet() {
        UUID sensorId = UUID.randomUUID();
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setStatus(Status.ALERT);
        SensorProperties.Thresholds thresholds = mock(SensorProperties.Thresholds.class);


        when(thresholds.getWarnLevel()).thenReturn(2000);
        when(thresholds.getTimes()).thenReturn(3);

        when(sensorProperties.getThresholds()).thenReturn(thresholds);

        when(sensorProperties.getThresholds().getWarnLevel()).thenReturn(2000);
        when(sensorProperties.getThresholds().getTimes()).thenReturn(3);

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        Measurement m1 = new Measurement(); m1.setCo2(1900);
        Measurement m2 = new Measurement(); m2.setCo2(1800);
        Measurement m3 = new Measurement(); m3.setCo2(1700);
        List<Measurement> measurements = Arrays.asList(m1, m2, m3);

        when(measurementRepository.findTop3BySensorOrderByTimeDesc(sensor)).thenReturn(measurements);

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

