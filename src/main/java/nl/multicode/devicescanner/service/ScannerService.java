package nl.multicode.devicescanner.service;

import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.multicode.devicescanner.config.DeviceScannerConfig;
import nl.multicode.devicescanner.model.DeviceRecord;
import nl.multicode.devicescanner.scanner.WifiScanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScannerService implements CommandLineRunner {

    private final WifiScanner scanner;
    private final CsvLogger logger;
    private final DeviceScannerConfig config;

    @Override
    public void run(String... args) {

        while (true) {
            try {
                System.out.println("Starting new scan...");
                List<DeviceRecord> results = scanner.scan();
                logger.write(results, new File(config.getOutputFile()));
                System.out.println("Devices found: " + results.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000); // Pause between scans
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
