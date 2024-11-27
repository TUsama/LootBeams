package com.lootbeams.modules.rarity.impl;

import com.lootbeams.modules.rarity.ILBRarity;
import net.minecraft.world.entity.item.ItemEntity;

import java.awt.*;
import java.util.function.Predicate;

public abstract class LBOutsideRarity implements ILBRarity {
    private final String name;
    private final Color color;
    private final Predicate<ItemEntity> condition;

    public LBOutsideRarity(String name, Color color, Predicate<ItemEntity> condition) {
        this.name = name;
        this.color = color;
        this.condition = condition;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }


}
