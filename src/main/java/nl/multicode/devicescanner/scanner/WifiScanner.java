package nl.multicode.devicescanner.scanner;

import nl.multicode.devicescanner.model.DeviceRecord;
import java.util.List;

public interface WifiScanner {
    List<DeviceRecord> scan();
}
