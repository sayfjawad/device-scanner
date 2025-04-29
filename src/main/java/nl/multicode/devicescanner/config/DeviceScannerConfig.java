package nl.multicode.devicescanner.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "scanner")
@Getter
@Setter
public class DeviceScannerConfig {

    private String outputFile;
    private String interfaceName;
    private int scanDuration;
    private int pauseDuration;
}
