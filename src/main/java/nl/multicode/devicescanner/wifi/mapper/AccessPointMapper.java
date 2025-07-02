package nl.multicode.devicescanner.wifi.mapper;

import lombok.extern.slf4j.Slf4j;
import nl.multicode.devicescanner.wifi.model.AccessPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccessPointMapper {

    public AccessPoint map(final String[] tokens) {

        AccessPoint ap = AccessPoint.builder()
                .bssid(tokens[0].trim())
                .firstSeen(tokens[1].trim())
                .lastSeen(tokens[2].trim())
                .channel(tokens[3].trim())
                .speed(tokens[4].trim())
                .privacy(tokens[5].trim())
                .cipher(tokens[6].trim())
                .authentication(tokens[7].trim())
                .power(parseInt(tokens[8]))
                .beacons(parseInt(tokens[9]))
                .iv(parseInt(tokens[10]))
                .lanIp(tokens[11].trim())
                .idLength(parseInt(tokens[12]))
                .essid((tokens[13].trim()))
                .key(tokens[14].trim())
                .build();
        return ap;
    }

    private int parseInt(String s) {

        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
