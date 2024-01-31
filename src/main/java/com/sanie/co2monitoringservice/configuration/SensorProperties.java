package com.sanie.co2monitoringservice.configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sensor")
public class SensorProperties {

    private final Thresholds thresholds = new Thresholds();

    public static class Thresholds {
        private int warnLevel;
        private int times;

        public int getWarnLevel() {
            return warnLevel;
        }

        public void setWarnLevel(int warnLevel) {
            this.warnLevel = warnLevel;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }
    }

    public Thresholds getThresholds() {
        return thresholds;
    }
}
