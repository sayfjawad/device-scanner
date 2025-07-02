package nl.multicode.devicescanner.wifi.model;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record DeviceRecord(String macAddress, String signalStrength, LocalDateTime timestamp) {

}
