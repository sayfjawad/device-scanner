package nl.multicode.devicescanner.wifi.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class DeviceRecord {

    private final String macAddress;
    private final String signalStrength;
    private final LocalDateTime timestamp;
}
