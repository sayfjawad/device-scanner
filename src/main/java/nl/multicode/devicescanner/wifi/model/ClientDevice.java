package nl.multicode.devicescanner.wifi.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientDevice {
    private String stationMac;
    private String firstSeen;
    private String lastSeen;
    private int power;
    private int packets;
    private String bssid;
    private List<String> probedEssids;
}
