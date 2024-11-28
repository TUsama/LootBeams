package com.lootbeams.data.rarity;

import com.lootbeams.data.LBItemEntity;
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
    ITEM((list, itemWithRarity) -> {
        for (Pair<String, Color> stringColorPair : list) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(stringColorPair.getFirst());
            if (resourceLocation == null) continue;
            Item registryItem = ForgeRegistries.ITEMS.getValue(resourceLocation);
            if (registryItem != null && registryItem.asItem() == itemWithRarity.item().getItem().getItem()) {
                return itemWithRarity.to(LBRarity.of(itemWithRarity.rarity().name(), stringColorPair.getSecond(), itemWithRarity.rarity().absoluteOrdinal()));
            }
        }
        return itemWithRarity;
    }),
    TAG((list, itemWithRarity) ->{
        for (Pair<String, Color> stringColorPair : list) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(stringColorPair.getFirst());
            if (resourceLocation == null) continue;
            if (ForgeRegistries.ITEMS.tags().getTag(TagKey.create(BuiltInRegistries.ITEM.key(), resourceLocation)).contains(itemWithRarity.item().getItem().getItem())) {
                return itemWithRarity.to(LBRarity.of(itemWithRarity.rarity().name(), stringColorPair.getSecond(), itemWithRarity.rarity().absoluteOrdinal()));
            }
        }
        return itemWithRarity;
    }),
    MODID((list, itemWithRarity) ->{
        for (Pair<String, Color> stringColorPair : list) {
            if (ForgeRegistries.ITEMS.getKey(itemWithRarity.item().getItem().getItem()).getNamespace().equals(stringColorPair.getFirst())) {
                return itemWithRarity.to(LBRarity.of(itemWithRarity.rarity().name(), stringColorPair.getSecond(), itemWithRarity.rarity().absoluteOrdinal()));
            }
        }
        return itemWithRarity;
    });

    public final BiFunction<List<Pair<String, Color>>, LBItemEntity, LBItemEntity> mutate;

    Order(BiFunction<List<Pair<String, Color>>, LBItemEntity, LBItemEntity> mutate) {
        this.mutate = mutate;
    }
}
