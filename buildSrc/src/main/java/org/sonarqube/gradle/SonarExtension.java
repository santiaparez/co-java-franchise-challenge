package org.sonarqube.gradle;

import org.gradle.api.Action;

import java.util.LinkedHashMap;
import java.util.Map;

public class SonarExtension {
    private final LinkedHashMap<String, Object> properties = new LinkedHashMap<>();

    public void properties(Action<? super PropertyBuilder> action) {
        PropertyBuilder builder = new PropertyBuilder(properties);
        action.execute(builder);
    }

    public Map<String, Object> getProperties() {
        return new LinkedHashMap<>(properties);
    }

    public static class PropertyBuilder {
        private final Map<String, Object> properties;

        PropertyBuilder(Map<String, Object> properties) {
            this.properties = properties;
        }

        public void property(String key, Object value) {
            properties.put(key, value);
        }
    }
}
