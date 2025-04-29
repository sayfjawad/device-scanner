package nl.multicode.devicescanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class WifiAirodumpScanner {

  private static final String OUTPUT_CSV = "/opt/device-scanner/data/devices-data.csv";
  private static final String TEMP_SCAN_FILE = "/tmp/scan"; // This is fine

  private static final String INTERFACE = "wlan1"; // WiFi interface name
  private static final int SCAN_DURATION_SECONDS = 60; // How long to scan each time
  private static final int PAUSE_BETWEEN_SCANS_MS = 2000; // Optional small pause

  public static void main(String[] args) {
    while (true) {
      try {
        System.out.println("Starting new scan at: " + LocalDateTime.now());

        startAirodumpScan();
        Map<String, String> devices = parseScanResults();
        saveDevicesToCsv(devices);

        System.out.println("Devices found: " + devices.size());
      } catch (Exception e) {
        e.printStackTrace();
      }

      pauseBeforeNextScan();
    }
  }

  private static void startAirodumpScan() throws IOException, InterruptedException {
    ProcessBuilder pb = new ProcessBuilder(
        "sudo", "airodump-ng",
        "--write-interval", "1",
        "--output-format", "csv",
        "-w", TEMP_SCAN_FILE,
        INTERFACE
    );
    Process process = pb.start();

    // Let it scan for the desired duration
    Thread.sleep(SCAN_DURATION_SECONDS * 1000);

    // Kill the process after scan duration
    process.destroy();
    process.waitFor();
  }

  private static Map<String, String> parseScanResults() {
    Map<String, String> devices = new HashMap<>();
    File csvFile = new File(TEMP_SCAN_FILE + "-01.csv");

    if (!csvFile.exists()) {
      System.err.println("Scan file not found: " + csvFile.getAbsolutePath());
      return devices;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
      String line;
      boolean readingStations = false;

      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }

        if (line.startsWith("Station MAC")) {
          readingStations = true;
          continue;
        }

        String[] parts = line.split(",");

        if (!readingStations) {
          // Parsing Access Points
          if (parts.length > 8 && isMacAddress(parts[0])) {
            String mac = parts[0].trim();
            String signal = parts[8].trim();
            if (isValidSignal(signal)) {
              devices.put(mac, signal);
            }
          }
        } else {
          // Parsing Stations (clients)
          if (parts.length > 3 && isMacAddress(parts[0])) {
            String mac = parts[0].trim();
            String signal = parts[3].trim();
            if (isValidSignal(signal)) {
              devices.put(mac, signal);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    cleanupTemporaryFiles();
    return devices;
  }

  private static void saveDevicesToCsv(Map<String, String> devices) {
    boolean fileExists = new File(OUTPUT_CSV).exists();
    String currentTime = LocalDateTime.now().toString(); // ISO 8601 format

    try (FileWriter writer = new FileWriter(OUTPUT_CSV, true)) {
      if (!fileExists) {
        writer.write("mac,signal_strength,date_time\n");
      }
      for (Map.Entry<String, String> device : devices.entrySet()) {
        String line = String.format("%s,%s,%s\n", device.getKey(), device.getValue(), currentTime);
        writer.write(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void pauseBeforeNextScan() {
    try {
      Thread.sleep(PAUSE_BETWEEN_SCANS_MS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Sleep interrupted: " + e.getMessage());
    }
  }

  private static boolean isMacAddress(String s) {
    return s.matches("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");
  }

  private static boolean isValidSignal(String signal) {
    return !signal.isEmpty() && !signal.equals("-1");
  }

  private static void cleanupTemporaryFiles() {
    deleteFileQuietly(TEMP_SCAN_FILE + "-01.csv");
    deleteFileQuietly(TEMP_SCAN_FILE + "-01.kismet.csv");
    deleteFileQuietly(TEMP_SCAN_FILE + "-01.kismet.netxml");
  }

  private static void deleteFileQuietly(String path) {
    File file = new File(path);
    if (file.exists()) {
      if (!file.delete()) {
        System.err.println("Failed to delete temp file: " + path);
      }
    }
  }
}
