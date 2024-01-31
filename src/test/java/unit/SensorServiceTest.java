package unit;

import com.sanie.co2monitoringservice.configuration.SensorProperties;
import com.sanie.co2monitoringservice.model.Sensor;
import com.sanie.co2monitoringservice.model.Status;
import com.sanie.co2monitoringservice.repository.SensorRepository;
import com.sanie.co2monitoringservice.service.AlertService;
import com.sanie.co2monitoringservice.service.SensorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private AlertService alertService;

    @Mock
    private SensorProperties sensorProperties;

    @InjectMocks
    private SensorService sensorService;

    @Test
    public void testUpdateSensorStatusAndHandleAlerts() {
        UUID sensorId = UUID.randomUUID();
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setStatus(Status.OK);
        SensorProperties.Thresholds thresholds = mock(SensorProperties.Thresholds.class);
        when(sensorProperties.getThresholds()).thenReturn(thresholds);
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(sensorProperties.getThresholds().getWarnLevel()).thenReturn(2000);
        when(sensorProperties.getThresholds().getTimes()).thenReturn(3);

        sensorService.updateSensorStatusAndHandleAlerts(sensorId, 2100);

        Mockito.verify(sensorRepository, times(1)).save(sensor);
    }

    @Test
    public void testSensorNotFound() {
        UUID sensorId = UUID.randomUUID();

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            sensorService.updateSensorStatusAndHandleAlerts(sensorId, 2100);
        });
    }
}

