package nl.multicode.devicescanner.wifi.scanner;

import nl.multicode.devicescanner.wifi.model.DeviceRecord;
import java.util.List;

public interface WifiScanner {
    List<DeviceRecord> scan();
}
