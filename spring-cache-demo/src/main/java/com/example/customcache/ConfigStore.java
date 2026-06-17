package com.example.customcache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigStore {
    private final List<Map<String, String>> propertySources = new ArrayList<>();

    public void addPropertySource(Map<String, String> source) {
        propertySources.add(0, Map.copyOf(source)); // add as highest precedence
    }

    public Optional<String> get(String key) {
        for (Map<String, String> src : propertySources) {
            if (src.containsKey(key)) {
                return Optional.of(src.get(key));
            }
        }
        return Optional.empty();
    }

    public String getOrDefault(String key, String def) {
        return get(key).orElse(def);
    }

    public String resolvePlaceholders(String value) {
        if (value == null) return null;
        String result = value;
        int start = result.indexOf("${");
        while (start != -1) {
            int end = result.indexOf('}', start);
            if (end == -1) break;
            String placeholder = result.substring(start + 2, end);
            String replace = getOrDefault(placeholder, "");
            result = result.substring(0, start) + replace + result.substring(end + 1);
            start = result.indexOf("${");
        }
        return result;
    }

}
