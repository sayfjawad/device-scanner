package nl.multicode.devicescanner.wifi.scanner;

import nl.multicode.devicescanner.wifi.model.WifiScanResult;

public interface WifiScanner {
    WifiScanResult scan(String scanningDeviceName);
}
