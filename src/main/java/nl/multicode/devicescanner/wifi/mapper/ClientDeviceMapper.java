package nl.multicode.devicescanner.wifi.mapper;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.multicode.devicescanner.wifi.model.ClientDevice;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientDeviceMapper {

    public ClientDevice map(final String[] tokens) {

        var clientDeviceBuilder = ClientDevice.builder()
                .stationMac(tokens[0].trim())
                .firstSeen(tokens[1].trim())
                .lastSeen(tokens[2].trim())
                .power(parseInt(tokens[3]))
                .packets(parseInt(tokens[4]))
                .bssid(tokens[5].trim());
        if (tokens.length > 6) {
            String[] essids = tokens[6].split(",");
            List<String> essidList = new ArrayList<>();
            for (String essid : essids) {
                String e = essid.trim();
                if (!e.isEmpty()) {
                    essidList.add(e);
                }
            }
            clientDeviceBuilder.probedEssids(essidList);
        }
        return clientDeviceBuilder.build();
    }

    private int parseInt(String s) {

        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
