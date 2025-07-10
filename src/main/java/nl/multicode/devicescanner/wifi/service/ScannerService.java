package nl.multicode.devicescanner.wifi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.multicode.devicescanner.config.DeviceScannerConfig;
import nl.multicode.devicescanner.wifi.model.WifiScanResult;
import nl.multicode.devicescanner.wifi.scanner.WifiScanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScannerService implements CommandLineRunner {

    private final WifiScanner scanner;
    private final DeviceScannerConfig config;

    @Override
    public void run(String... args) {

        while (true) {
            try {
                System.out.println("Starting new scan...");
                WifiScanResult results = scanner.scan(config.getInterfaceName());
                log.info("Found {} client devices & {} access points", results.getClients().size(),
                        results.getAccessPoints().size());
//                logger.write(results, new File(config.getOutputFile()));
//                System.out.println("Devices found: " + results.size());
            } catch (Exception e) {
                log.error("Error during scan", e);
            }
            try {
                Thread.sleep(config.getPauseDuration()); // Pause between scans
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
