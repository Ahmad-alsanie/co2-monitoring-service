package unit;

import com.sanie.co2monitoringservice.configuration.SensorProperties;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.repository.SensorRepository;
import com.sanie.co2monitoringservice.service.AlertService;
import com.sanie.co2monitoringservice.service.SensorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private SensorProperties sensorProperties;

    @InjectMocks
    private SensorService sensorService;

    @Mock
    private AlertService alertService;

    private Sensor sensor;
    private UUID sensorId;
    private UUID invalidSensorId;

    private final static int THRESHOLD = 2000;
    private final static int TIMES = 3;

    @BeforeEach
    public void setup(){
        sensorId = UUID.randomUUID();
        invalidSensorId = UUID.randomUUID();
        sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setStatus(Status.OK);
        SensorProperties.Thresholds thresholds = mock(SensorProperties.Thresholds.class);
        when(sensorProperties.getThresholds()).thenReturn(thresholds);
        lenient().when(sensorProperties.getThresholds().getWarnLevel()).thenReturn(THRESHOLD);
        when(sensorProperties.getThresholds().getTimes()).thenReturn(TIMES);
    }

    @Test
    public void testUpdateSensorStatusAndHandleAlerts() {

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        sensorService.updateSensorStatusAndHandleAlerts(sensorId, 2100);

        Mockito.verify(sensorRepository, times(1)).save(sensor);
    }

    @Test
    public void testSensorNotFound() {

        when(sensorRepository.findById(invalidSensorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            sensorService.updateSensorStatusAndHandleAlerts(invalidSensorId, 2100);
        });
    }

    @Test
    public void testDetermineStatusReturnsAlertWhenThresholdExceeded() {
        List<Integer> measurements = Arrays.asList(2001, 2003, 2007);
        Status result = sensorService.determineStatus(measurements, Status.OK);
        assertEquals(Status.ALERT, result);
    }

    @Test
    public void testDetermineStatusReturnsWarnWhenLastMeasurementExceedsAndPreviousNotAlert() {
        List<Integer> measurements = Arrays.asList(1900, 1800, 2100);
        Status result = sensorService.determineStatus(measurements, Status.OK);
        assertEquals(Status.WARN, result);
    }

    @Test
    public void testDetermineStatusReturnsOkWhenMeasurementsDoNotExceedThreshold() {
        List<Integer> measurements = Arrays.asList(1800, 1900, 2000);
        Status result = sensorService.determineStatus(measurements, Status.WARN);
        assertEquals(Status.OK, result);
    }

    @Test
    public void testDetermineStatusRemainsAlertRegardlessOfMeasurements() {
        List<Integer> measurements = Arrays.asList(1000, 1500, 1800);
        Status result = sensorService.determineStatus(measurements, Status.ALERT);
        assertEquals(Status.ALERT, result);
    }

    @Test
    public void testUpdateStatusCreatesAlertWhenStatusChangesToAlert() {
        Sensor sensor = new Sensor();
        sensor.setStatus(Status.OK);
        List<Integer> measurements = Arrays.asList(2100, 2200, 2300);

        sensorService.updateStatus(sensor, measurements);

        verify(sensorRepository, times(1)).save(sensor);
        verify(alertService, times(1)).createAlert(any(), eq(measurements));
        assertEquals(Status.ALERT, sensor.getStatus());
    }

    @Test
    public void testUpdateStatusSavesSensorWithoutAlertWhenStatusChangesToWarn() {
        Sensor sensor = new Sensor();
        sensor.setStatus(Status.OK);
        List<Integer> measurements = Arrays.asList(1900, 1800, 2100);

        sensorService.updateStatus(sensor, measurements);

        verify(sensorRepository, times(1)).save(sensor);
        verify(alertService, never()).createAlert(any(), any());
        assertEquals(Status.WARN, sensor.getStatus());
    }

    @Test
    public void testUpdateStatusDoesNothingWhenStatusRemainsSame() {
        Sensor sensor = new Sensor();
        sensor.setStatus(Status.OK);
        List<Integer> measurements = Arrays.asList(1800, 1700, 1900);

        sensorService.updateStatus(sensor, measurements);

        verify(sensorRepository, never()).save(sensor);
        verify(alertService, never()).createAlert(any(), any());
        assertEquals(Status.OK, sensor.getStatus());
    }

}

