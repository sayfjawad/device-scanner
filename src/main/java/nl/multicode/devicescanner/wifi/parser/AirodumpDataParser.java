package nl.multicode.devicescanner.wifi.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nl.multicode.devicescanner.wifi.mapper.AccessPointMapper;
import nl.multicode.devicescanner.wifi.mapper.ClientDeviceMapper;
import nl.multicode.devicescanner.wifi.model.AccessPoint;
import nl.multicode.devicescanner.wifi.model.ClientDevice;
import nl.multicode.devicescanner.wifi.model.WifiScanResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AirodumpDataParser {

    private final AccessPointMapper accessPointMapper;
    private final ClientDeviceMapper deviceMapper;
    private final ClientDeviceMapper clientDeviceMapper;

    public WifiScanResult parse(File file) {

        List<AccessPoint> accessPoints = new ArrayList<>();
        List<ClientDevice> clientDevices = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean readingClients = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("Station MAC")) {
                    readingClients = true;
                    continue;
                }
                if (line.startsWith("BSSID") || line.startsWith("Station MAC")) {
                    continue; // skip headers
                }
                if (!readingClients) {
                    // Access Point parsing
                    String[] tokens = line.split(",", -1);
                    if (tokens.length < 15) {
                        continue;
                    }
                    AccessPoint ap = accessPointMapper.map(tokens);
                    accessPoints.add(ap);
                } else {
                    // Client Device parsing
                    String[] tokens = line.split(",", -1);
                    if (tokens.length < 6) {
                        continue;
                    }
                    clientDevices.add(clientDeviceMapper.map(tokens));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Fout bij parsen van CSV-bestand", e);
        }
        WifiScanResult result = new WifiScanResult();
        result.setAccessPoints(accessPoints);
        result.setClients(clientDevices);

        return result;
    }
}
