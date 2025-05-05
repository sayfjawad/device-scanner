package nl.multicode.devicescanner.wifi.service;

import lombok.extern.slf4j.Slf4j;
import nl.multicode.devicescanner.wifi.model.DeviceRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CsvLogger {



    public void write(List<DeviceRecord> devices, File outputFile) {
        boolean fileExists = outputFile.exists();

        try (FileWriter writer = new FileWriter(outputFile, true)) {
            if (!fileExists) {
                writer.write("mac,signal_strength,date_time\n");
            }

            for (DeviceRecord device : devices) {
                final String csvRecord = String.format("%s,%s,%s%n",
                        device.getMacAddress(),
                        device.getSignalStrength(),
                        device.getTimestamp().toString());
                log.info(String.format("( %s )",csvRecord));
                writer.write(csvRecord);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
