package nl.multicode.devicescanner.service;

import nl.multicode.devicescanner.model.DeviceRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CsvLogger {



    public void write(List<DeviceRecord> devices, File outputFile) {
        boolean fileExists = outputFile.exists();

        try (FileWriter writer = new FileWriter(outputFile, true)) {
            if (!fileExists) {
                writer.write("mac,signal_strength,date_time\n");
            }

            for (DeviceRecord device : devices) {
                writer.write(String.format("%s,%s,%s%n",
                        device.getMacAddress(),
                        device.getSignalStrength(),
                        device.getTimestamp().toString()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
