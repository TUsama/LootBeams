package com.lootbeams.modules.rarity;

import net.minecraft.world.entity.item.ItemEntity;

import java.awt.*;

public interface ILBRarity {

    String getName();
    Color getColor();
    boolean isThisRarity(ItemEntity itemEntity);
}
