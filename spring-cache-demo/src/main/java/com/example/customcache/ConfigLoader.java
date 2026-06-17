package com.example.customcache;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigLoader {

    public Map<String, String> loadProperties(Path path) throws IOException {
        if (!Files.exists(path)) {
            return Map.of();
        }
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(path.toFile())) {
            props.load(in);
        }
        Map<String, String> map = new HashMap<>();
        for (String name : props.stringPropertyNames()) {
            map.put(name, props.getProperty(name));
        }
        return map;
    }
}
