package nl.multicode.devicescanner.parser;

import nl.multicode.devicescanner.model.DeviceRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AirodumpCsvParser {

    public List<DeviceRecord> parse(File csvFile, LocalDateTime timestamp) {
        List<DeviceRecord> devices = new ArrayList<>();

        if (!csvFile.exists()) {
            System.err.println("CSV file does not exist: " + csvFile.getAbsolutePath());
            return devices;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean readingStations = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Station MAC")) {
                    readingStations = true;
                    continue;
                }

                String[] parts = line.split(",");

                if (!readingStations) {
                    // Access Point section
                    if (parts.length > 8 && isMacAddress(parts[0])) {
                        String mac = parts[0].trim();
                        String signal = parts[8].trim();
                        if (isValidSignal(signal)) {
                            devices.add(new DeviceRecord(mac, signal, timestamp));
                        }
                    }
                } else {
                    // Station section
                    if (parts.length > 3 && isMacAddress(parts[0])) {
                        String mac = parts[0].trim();
                        String signal = parts[3].trim();
                        if (isValidSignal(signal)) {
                            devices.add(new DeviceRecord(mac, signal, timestamp));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return devices;
    }

    private boolean isMacAddress(String s) {
        return s.matches("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");
    }

    private boolean isValidSignal(String signal) {
        return !signal.isEmpty() && !signal.equals("-1");
    }
}
