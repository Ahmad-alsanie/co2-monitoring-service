package unit;

import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.repository.MeasurementRepository;
import com.sanie.co2monitoringservice.service.AlertService;
import com.sanie.co2monitoringservice.service.MeasurementService;
import com.sanie.co2monitoringservice.service.SensorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeasurementServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private SensorService sensorService;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private MeasurementService measurementService;

    private UUID sensorId;
    private UUID invalidSensorId;
    private Sensor sensor;

    private static final int CO2_LEVEL = 2000;
    private static final int CO2_LEVEL_UNDER_ALARM = 1500;

    @BeforeEach
    public void setup(){
        sensorId = UUID.randomUUID();
        invalidSensorId = UUID.randomUUID();
        sensor = new Sensor();
        sensor.setId(sensorId);
    }

    @Test
    public void testRecordMeasurement() {
        LocalDateTime time = LocalDateTime.now();

        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        when(sensorService.findSensorById(sensorId)).thenReturn(Optional.of(sensor));

        measurementService.recordMeasurement(sensorId, CO2_LEVEL, time);

        verify(measurementRepository, times(1)).save(any(Measurement.class));
        verify(sensorService, times(1)).updateSensorStatusAndHandleAlerts(sensorId, CO2_LEVEL);
        verify(alertService, times(1)).clearAlertIfConditionsMet(sensorId);
    }

    @Test
    public void testSensorNotFound() {
        LocalDateTime time = LocalDateTime.now();
        when(sensorService.findSensorById(invalidSensorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            measurementService.recordMeasurement(invalidSensorId, CO2_LEVEL, time);
        });
    }

    @Test
    public void testNoAlertConditionMet() {
        LocalDateTime time = LocalDateTime.now();

        when(sensorService.findSensorById(sensorId)).thenReturn(Optional.of(sensor));

        measurementService.recordMeasurement(sensorId, CO2_LEVEL_UNDER_ALARM, time);

        verify(measurementRepository, times(1)).save(any(Measurement.class));

        verify(sensorService, times(1)).updateSensorStatusAndHandleAlerts(sensorId, CO2_LEVEL_UNDER_ALARM);

        verify(alertService, times(1)).clearAlertIfConditionsMet(sensorId);

        verify(alertService, never()).createAlert(any(UUID.class), anyList());
    }

    @Test
    public void testMeasurementSaveFailure() {
        LocalDateTime time = LocalDateTime.now();

        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        when(sensorService.findSensorById(sensorId)).thenReturn(Optional.of(sensor));

        doThrow(new RuntimeException("Database error")).when(measurementRepository).save(any(Measurement.class));

        assertThrows(RuntimeException.class, () -> measurementService.recordMeasurement(sensorId, CO2_LEVEL, time));
    }
}
