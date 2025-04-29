package nl.multicode.devicescanner;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WifiAirodumpScanner {

  private static final String INTERFACE = "wlan1"; // Jouw WiFi interface
  private static final String OUTPUT_CSV = "device_log.csv"; // Output bestand
  private static final int SCAN_DURATION_SECONDS = 60; // Scan duur in seconden

  public static void main(String[] args) {
    while (true) {
      try {
        System.out.println("Nieuwe scan gestart om: " + LocalDateTime.now());
        Map<String, String> devices = scanWifiDevices();
        writeDevicesToCsv(devices);
        System.out.println("Aantal gevonden devices: " + devices.size());
      } catch (Exception e) {
        e.printStackTrace();
      }

      // Eventueel korte pauze tussen scans
      try {
        Thread.sleep(2000); // 2 seconden wachten voor volgende scan
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private static Map<String, String> scanWifiDevices() throws Exception {
    Map<String, String> devices = new HashMap<>();
    String scanFile = "/tmp/scan";

    // Start airodump-ng
    ProcessBuilder pb = new ProcessBuilder(
        "sudo", "airodump-ng",
        "--write-interval", "1",
        "--output-format", "csv",
        "-w", scanFile,
        INTERFACE
    );
    Process process = pb.start();

    // Laat het proces lopen voor een bepaalde tijd
    Thread.sleep(SCAN_DURATION_SECONDS * 1000);

    // Stop het proces netjes
    process.destroy();
    process.waitFor();

    // Parse het gegenereerde CSV bestand
    File csvFile = new File("/tmp/scan-01.csv");
    if (csvFile.exists()) {
      BufferedReader reader = new BufferedReader(new FileReader(csvFile));
      String line;
      boolean readingStations = false;

      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) continue;
        if (line.startsWith("Station MAC")) {
          readingStations = true;
          continue;
        }
        if (readingStations) {
          String[] parts = line.split(",");
          if (parts.length >= 4) {
            String mac = parts[0].trim();
            String signal = parts[3].trim(); // Signaalsterkte veld
            devices.put(mac, signal);
          }
        }
      }
      reader.close();
    }

    // Verwijder oude scan files zodat volgende scan opnieuw kan
    new File("/tmp/scan-01.csv").delete();
    new File("/tmp/scan-01.kismet.csv").delete();
    new File("/tmp/scan-01.kismet.netxml").delete();

    return devices;
  }

  private static void writeDevicesToCsv(Map<String, String> devices) {
    boolean fileExists = new File(OUTPUT_CSV).exists();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    String now = LocalDateTime.now().toString(); // Je zou ISO 8601 met zone kunnen gebruiken als nodig

    try (FileWriter writer = new FileWriter(OUTPUT_CSV, true)) {
      if (!fileExists) {
        writer.write("mac,signal_strength,date_time\n"); // Schrijf header als bestand nieuw is
      }
      for (Map.Entry<String, String> device : devices.entrySet()) {
        writer.write(String.format("%s,%s,%s\n", device.getKey(), device.getValue(), now));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
