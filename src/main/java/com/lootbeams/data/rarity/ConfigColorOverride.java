package com.lootbeams.data.rarity;

import com.google.common.collect.ImmutableMap;
import com.lootbeams.LootBeams;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.data.LBItemEntity;
import com.lootbeams.utils.Attempt;
import com.mojang.datafixers.util.Pair;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigColorOverride {

    static final List<String> list = Arrays.stream(Order.values())
            .map(Enum::toString)
            .toList();

    static final Map<Order, List<Pair<String, Color>>> configMap;

    static {
        List<String> overrides = ConfigurationManager.request(Config.COLOR_OVERRIDES);
        if (overrides.isEmpty()) {
            configMap = ImmutableMap.of();
        } else {
            configMap = overrides.stream().filter((s) -> (!s.isEmpty()))
                    .map(s -> s.split("="))
                    .filter(strings -> strings.length == 2)
                    //validate color
                    .filter(strings -> !Attempt.hasException(() -> Color.decode(strings[1]), () -> LootBeams.LOGGER.error(String.format("Color overrides error! \"%s\" is not a valid hex color for \"%s\"", strings[1], strings[0]))))
                    .map(strings -> Pair.of(strings[0], Color.decode(strings[1])))
                    .collect(Collectors.groupingBy(x -> {
                        String name = x.getFirst();
                        if (!name.contains(":")) return Order.MODID;
                        if (name.startsWith("#")) return Order.TAG;
                        return Order.ITEM;
                    }));
        }
    }

    public static LBItemEntity tryGetConfigRarity(LBItemEntity LBItemEntity) {

        List<String> overrides = ConfigurationManager.request(Config.COLOR_OVERRIDES);
        List<String> order = ConfigurationManager.request(Config.COLOR_APPLY_ORDER);
        if (!overrides.isEmpty()) {

            return order.stream()
                    .filter(x -> !list.contains(x))
                    .map(Order::valueOf)
                    .filter(order1 -> configMap.get(order1) != null)
                    .map(order1 -> order1.mutate.apply(configMap.get(order1), LBItemEntity))
                    .findFirst()
                    .orElse(LBItemEntity);


        }
        return LBItemEntity;
    }
}
