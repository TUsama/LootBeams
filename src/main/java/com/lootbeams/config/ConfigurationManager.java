package com.lootbeams.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigurationManager {

    private static Map<Config, Supplier<Object>> connection = new EnumMap<>(Config.class);

    public static Runnable fill;

    public static void insert(Config config, Supplier<Object> object) {
        connection.put(config, object);
    }


    public static <T> T request(Config config) {
        if (connection.isEmpty()) fill.run();
        if (connection.containsKey(config)) {
            Object value = connection.get(config).get();
            Type expectedType = config.getType();

            if (isCompatibleType(value, expectedType)) {
                return (T) value;
            } else {
                throw new ClassCastException("The value for " + config + " cannot be cast to " + expectedType.getTypeName());
            }
        } else {
            throw new RuntimeException("Can't find this config! " + config);
        }
    }

    private static boolean isCompatibleType(Object value, Type expectedType) {
        if (expectedType instanceof Class<?>) {
            return ((Class<?>) expectedType).isInstance(value);
        } else if (expectedType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) expectedType;
            if (parameterizedType.getRawType() instanceof Class<?> &&
                    ((Class<?>) parameterizedType.getRawType()).isInstance(value)) {

                //List<String>
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length == 1 && actualTypeArguments[0] == String.class &&
                        value instanceof List<?>) {
                    List<?> list = (List<?>) value;
                    return list.stream().allMatch(item -> item instanceof String);
                }


            }
        }
        return false;
    }


}
