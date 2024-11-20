package com.lootbeams.modules.beam.color;

import net.minecraft.world.entity.item.ItemEntity;

import java.awt.*;

@FunctionalInterface
public interface IBeamColorSource<T>{
    Color DEFAULT = Color.WHITE;

    Color getColor(ItemEntity item);
}
