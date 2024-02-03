package com.sanie.co2monitoringservice.dto;

public class SensorMetricsDTO {
    private double maxLast30Days;
    private double avgLast30Days;

    public SensorMetricsDTO(double maxLast30Days, double avgLast30Days) {
        this.maxLast30Days = maxLast30Days;
        this.avgLast30Days = avgLast30Days;
    }

    public double getMaxLast30Days() {
        return maxLast30Days;
    }

    public void setMaxLast30Days(double maxLast30Days) {
        this.maxLast30Days = maxLast30Days;
    }

    public double getAvgLast30Days() {
        return avgLast30Days;
    }

    public void setAvgLast30Days(double avgLast30Days) {
        this.avgLast30Days = avgLast30Days;
    }
}
