package nl.multicode.devicescanner.wifi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class AccessPoint {
    private String bssid;
    private String firstSeen;
    private String lastSeen;
    private String channel;
    private String speed;
    private String privacy;
    private String cipher;
    private String authentication;
    private int power;
    private int beacons;
    private int iv;
    private String lanIp;
    private int idLength;
    private String essid;
    private String key;
}
