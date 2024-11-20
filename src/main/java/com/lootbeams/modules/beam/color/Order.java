package com.lootbeams.modules.beam.color;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;

public enum Order {
    ITEM((list, either) -> {
        for (Pair<String, Color> stringColorPair : list) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(stringColorPair.getFirst());
            if (resourceLocation == null) continue;
            Item registryItem = ForgeRegistries.ITEMS.getValue(resourceLocation);
            if (registryItem != null && registryItem.asItem() == either.left().get()) {
                return Either.right(stringColorPair.getSecond());
            }
        }
        return either;
    }),
    TAG((list, either) ->{
        for (Pair<String, Color> stringColorPair : list) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(stringColorPair.getFirst());
            if (resourceLocation == null) continue;
            if (ForgeRegistries.ITEMS.tags().getTag(TagKey.create(BuiltInRegistries.ITEM.key(), resourceLocation)).contains(either.left().get())) {
                return Either.right(stringColorPair.getSecond());
            }
        }
        return either;
    }),
    MODID((list, either) ->{
        for (Pair<String, Color> stringColorPair : list) {
            if (ForgeRegistries.ITEMS.getKey(either.left().get()).getNamespace().equals(stringColorPair.getFirst())) {
                return Either.right(stringColorPair.getSecond());
            }
        }
        return either;
    });

    public final BiFunction<List<Pair<String, Color>>, Either<Item, Color>, Either<Item, Color>> mutate;

    Order(BiFunction<List<Pair<String, Color>>, Either<Item, Color>, Either<Item, Color>> mutate) {
        this.mutate = mutate;
    }
}
