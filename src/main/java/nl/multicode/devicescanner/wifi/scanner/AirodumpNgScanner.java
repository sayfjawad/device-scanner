package nl.multicode.devicescanner.wifi.scanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.multicode.devicescanner.wifi.model.WifiScanResult;
import nl.multicode.devicescanner.wifi.parser.AirodumpDataParser;
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
            File latestCsvFile = getLatestScanCsvFile(new File(SCAN_DIR));
            return parser.parse(latestCsvFile);
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
        deleteByPattern("scan-\\d{2}\\.csv");
        deleteByPattern("scan-\\d{2}\\.kismet\\.csv");
        deleteByPattern("scan-\\d{2}\\.kismet\\.netxml");
    }

    private void deleteByPattern(String pattern) {
        File[] files = new File(SCAN_DIR).listFiles((dir, name) -> name.matches(pattern));
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    System.err.println("Warning: kon bestand niet verwijderen: " + file.getAbsolutePath());
                }
            }
        }
    }

    private File getLatestScanCsvFile(File directory) {

        File[] files = directory.listFiles((dir, name) -> name.matches("scan-\\d{2}\\.csv"));
        if (files == null || files.length == 0) {
            throw new RuntimeException(
                    "Geen scan CSV-bestand gevonden in: " + directory.getAbsolutePath());
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0]; // meest recent gewijzigde bestand
    }
}
