package nl.multicode.devicescanner.scanner;

import lombok.RequiredArgsConstructor;
import nl.multicode.devicescanner.model.DeviceRecord;
import nl.multicode.devicescanner.parser.AirodumpCsvParser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AirodumpNgScanner implements WifiScanner {

    private static final String INTERFACE = "wlxd03745acbed8";
    private static final int SCAN_DURATION_SECONDS = 60;
    private static final String TEMP_SCAN_FILE = "/tmp/scan";
    private final AirodumpCsvParser parser;


    @Override
    public List<DeviceRecord> scan() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "sudo", "airodump-ng",
                    "--write-interval", "1",
                    "--output-format", "csv",
                    "-w", TEMP_SCAN_FILE,
                    INTERFACE
            );
            Process process = pb.start();
            Thread.sleep(SCAN_DURATION_SECONDS * 1000);
            process.destroy();
            process.waitFor();

            return parser.parse(new File(TEMP_SCAN_FILE + "-01.csv"), LocalDateTime.now());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Scanning failed", e);
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        delete("/tmp/scan-01.csv");
        delete("/tmp/scan-01.kismet.csv");
        delete("/tmp/scan-01.kismet.netxml");
    }

    private void delete(String path) {
        File file = new File(path);
        if (file.exists() && !file.delete()) {
            System.err.println("Warning: could not delete " + path);
        }
    }
}
