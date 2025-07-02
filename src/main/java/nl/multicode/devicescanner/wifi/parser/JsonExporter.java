package nl.multicode.devicescanner.wifi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.multicode.devicescanner.wifi.model.DeviceRecord;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonExporter {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public void exportToJson(List<DeviceRecord> records, File outputFile) {
        try {
            objectMapper.writeValue(outputFile, records);
            System.out.println("JSON geschreven naar: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Fout bij wegschrijven JSON", e);
        }
    }
}
