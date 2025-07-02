package nl.multicode.devicescanner.wifi.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.multicode.devicescanner.wifi.model.WifiScanResult;
import nl.multicode.devicescanner.wifi.parser.AirodumpDataParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AirodumpNgScanner implements WifiScanner {

    private static final int SCAN_DURATION_SECONDS = 60;
    private static final String SCAN_DIR = "./scans";
    private static final String TEMP_SCAN_FILE = SCAN_DIR + "/scan";
    private final AirodumpDataParser parser;

    @Override
    public WifiScanResult scan(final String wifiAdapterName) {
        ensureScanDirExists();

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "sudo", "airodump-ng",
                    "--write-interval", "1",
                    "--output-format", "csv",
                    "-w", TEMP_SCAN_FILE,
                    wifiAdapterName
            );
            //pb.inheritIO(); // toont output in je console (optioneel)
            Process process = pb.start();
            Thread.sleep(SCAN_DURATION_SECONDS * 1000);
            process.destroy();
            process.waitFor();

            return parser.parse(new File(TEMP_SCAN_FILE + "-01.csv"));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Scanning failed", e);
        } finally {
            cleanup();
        }
    }

    private void ensureScanDirExists() {
        try {
            Files.createDirectories(new File(SCAN_DIR).toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create scan directory", e);
        }
    }

    private void cleanup() {
        delete(TEMP_SCAN_FILE + "-01.csv");
        delete(TEMP_SCAN_FILE + "-01.kismet.csv");
        delete(TEMP_SCAN_FILE + "-01.kismet.netxml");
    }

    private void delete(String path) {
        File file = new File(path);
        if (file.exists() && !file.delete()) {
            log.error("Warning: could not delete {}",  path);
        }
    }
}
