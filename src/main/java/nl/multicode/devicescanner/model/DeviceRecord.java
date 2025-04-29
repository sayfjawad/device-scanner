package nl.multicode.devicescanner.model;

import java.time.LocalDateTime;

public class DeviceRecord {
    private final String macAddress;
    private final String signalStrength;
    private final LocalDateTime timestamp;

    public DeviceRecord(String macAddress, String signalStrength, LocalDateTime timestamp) {
        this.macAddress = macAddress;
        this.signalStrength = signalStrength;
        this.timestamp = timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getSignalStrength() {
        return signalStrength;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
