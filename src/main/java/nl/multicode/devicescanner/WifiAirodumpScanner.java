package nl.multicode.devicescanner;

import java.io.*;
import java.util.*;

public class WifiAirodumpScanner {

  public static List<String> scanWifiDevices() {
    List<String> devices = new ArrayList<>();
    try {
      // Start airodump-ng and dump to CSV
      ProcessBuilder pb = new ProcessBuilder(
          "sudo", "airodump-ng", "--write-interval", "1", "--output-format", "csv", "-w", "/tmp/scan", "wlan1"
      );
      pb.redirectErrorStream(true);
      Process process = pb.start();

      // Sleep a few seconds to let airodump-ng gather data
      Thread.sleep(5000);

      // Kill airodump-ng after few seconds
      process.destroy();

      // Now parse the CSV
      File csvFile = new File("/tmp/scan-01.csv");
      if (csvFile.exists()) {
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String line;
        boolean readingStations = false;

        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (line.isEmpty()) continue;
          if (line.startsWith("Station MAC")) {
            readingStations = true; // Stations come after this line
            continue;
          }
          if (readingStations) {
            String[] parts = line.split(",");
            if (parts.length > 0) {
              devices.add(parts[0].trim()); // Station MAC address
            }
          }
        }
        reader.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return devices;
  }

  public static void main(String[] args) {
    List<String> devices = scanWifiDevices();
    System.out.println("WiFi Devices Found: " + devices.size());
    for (String device : devices) {
      System.out.println(" - " + device);
    }
  }
}
