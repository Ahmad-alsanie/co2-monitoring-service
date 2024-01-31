package unit;

import com.sanie.co2monitoringservice.model.Measurement;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.repository.MeasurementRepository;
import com.sanie.co2monitoringservice.service.AlertService;
import com.sanie.co2monitoringservice.service.MeasurementService;
import com.sanie.co2monitoringservice.service.SensorService;
import jakarta.persistence.EntityNotFoundException;
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

    @Test
    public void testRecordMeasurement() {
        UUID sensorId = UUID.randomUUID();
        int co2 = 2000;
        LocalDateTime time = LocalDateTime.now();

        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        when(sensorService.findSensorById(sensorId)).thenReturn(Optional.of(sensor));

        measurementService.recordMeasurement(sensorId, co2, time);

        verify(measurementRepository, times(1)).save(any(Measurement.class));
        verify(sensorService, times(1)).updateSensorStatusAndHandleAlerts(sensorId, co2);
        verify(alertService, times(1)).clearAlertIfConditionsMet(sensorId);
    }

    @Test
    public void testSensorNotFound() {
        UUID sensorId = UUID.randomUUID();
        int co2 = 2000;
        LocalDateTime time = LocalDateTime.now();
        when(sensorService.findSensorById(sensorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            measurementService.recordMeasurement(sensorId, co2, time);
        });
    }

    @Test
    public void testNoAlertConditionMet() {
        UUID sensorId = UUID.randomUUID();
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        int co2LevelNotTriggeringAlert = 1500;
        LocalDateTime time = LocalDateTime.now();

        when(sensorService.findSensorById(sensorId)).thenReturn(Optional.of(sensor));

        measurementService.recordMeasurement(sensorId, co2LevelNotTriggeringAlert, time);

        verify(measurementRepository, times(1)).save(any(Measurement.class));

        verify(sensorService, times(1)).updateSensorStatusAndHandleAlerts(sensorId, co2LevelNotTriggeringAlert);

        verify(alertService, times(1)).clearAlertIfConditionsMet(sensorId);

        verify(alertService, never()).createAlert(any(UUID.class), anyList());
    }

    @Test
    public void testMeasurementSaveFailure() {
        UUID sensorId = UUID.randomUUID();
        int co2 = 2000;
        LocalDateTime time = LocalDateTime.now();

        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        when(sensorService.findSensorById(sensorId)).thenReturn(Optional.of(sensor));

        doThrow(new RuntimeException("Database error")).when(measurementRepository).save(any(Measurement.class));

        assertThrows(RuntimeException.class, () -> measurementService.recordMeasurement(sensorId, co2, time));
    }
}
