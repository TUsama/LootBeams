package com.lootbeams.config;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigurationManager {

    public static Runnable fill;
    private static Map<Config, Supplier<Object>> connection = new EnumMap<>(Config.class);

    public static void insert(Config config, Supplier<Object> object) {
        connection.put(config, object);
    }


    public static <T> T request(Config config) {
        if (connection.isEmpty()) fill.run();
        if (connection.containsKey(config)) {
            Object value = connection.get(config).get();
            return (T) value;
        }
        return null;
    }

    public static <T> T request(Class<T> clazz, Config config) {
        if (connection.isEmpty()) fill.run();
        if (connection.containsKey(config)) {
            Object value = connection.get(config).get();

            return (T) value;

        }
        return null;
    }
}
