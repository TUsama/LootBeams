package com.lootbeams.modules.beam.color;

import com.google.common.collect.ImmutableMap;
import com.lootbeams.LootBeams;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.utils.Attempt;
import com.lootbeams.utils.Provider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import lombok.val;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum LBBuildInColorSource implements IBeamColorSource<Item> {
    //todo need test the time cost of this part cuz it is render-related.
    CONFIG((itemEntity, optionalColor) -> {
        List<String> overrides = ConfigurationManager.request(Config.COLOR_OVERRIDES);
        List<String> order = ConfigurationManager.request(Config.COLOR_APPLY_ORDER);
        Item item1 = itemEntity.getItem().getItem();
        System.out.println("override is " + overrides);
        if (!overrides.isEmpty()) {


            Either<Item, Color> either = Either.left(item1);

            order.stream()
                    .filter(x -> !Container.list.contains(x))
                    .map(Order::valueOf)
                    .filter(order1 -> Container.configMap.get(order1) != null)
                    .map(order1 -> order1.mutate.apply(Container.configMap.get(order1), either))
                    .filter(x -> x.right().isEmpty())
                    .findFirst()
                    .ifPresent(x -> optionalColor.map(y -> x.right()));

        }
        return optionalColor;
    }),
    NAME((itemEntity, optionalColor) -> {
        Boolean useNameColor = ConfigurationManager.request(Config.RENDER_NAME_COLOR);
        if (useNameColor) {
            return Optional.of(Provider.getRawColor(itemEntity.getItem().getHoverName()));
        }
        return optionalColor;
    }),
    RARITY((itemEntity, optionalColor) -> {
        boolean useRarity = ConfigurationManager.request(Config.RENDER_RARITY_COLOR);
        val color = itemEntity.getItem().getRarity().getStyleModifier().apply(Style.EMPTY).getColor();
        val hasColor = color != null;
        System.out.println(useRarity);
        System.out.println(hasColor);
        if (useRarity && hasColor) {
            return optionalColor.map(x -> new Color(color.getValue()));
        }
        return optionalColor;
    });

    final BiFunction<ItemEntity, Optional<Color>, Optional<Color>> func;


    LBBuildInColorSource(BiFunction<ItemEntity, Optional<Color>, Optional<Color>> func) {
        this.func = func;
    }

    @Override
    public Color getColor(ItemEntity item) {
        Optional<Color> defaultColor = Optional.of(DEFAULT);

        return func.apply(item, defaultColor).orElse(DEFAULT);
    }




    private static class Container {
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
    }
}
