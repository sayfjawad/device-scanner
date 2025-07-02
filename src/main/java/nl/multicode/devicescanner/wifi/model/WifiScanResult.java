package nl.multicode.devicescanner.wifi.model;

import java.util.List;
import lombok.Data;

@Data
public class WifiScanResult {
    private List<AccessPoint> accessPoints;
    private List<ClientDevice> clients;
}
