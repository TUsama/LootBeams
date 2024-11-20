package com.lootbeams.modules.beam.color;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.WeakHashMap;

public class BeamColorCache {
    private final static WeakHashMap<ItemStack, Color> colorMap = new WeakHashMap<>(500);

    public static Either<Boolean, Color> ask(ItemEntity entity){
        ItemStack item = entity.getItem();
        if (colorMap.containsKey(item)) {
            return Either.right(colorMap.get(item));
        }
        Color itemColor = BeamColorSourceContainer.getItemColor(entity);
        provide(entity, itemColor);
        return Either.right(itemColor);
    }

    protected static boolean provide(ItemEntity entity, Color color){
        if (colorMap.containsKey(entity.getItem())){
            return false;
        }
        colorMap.put(entity.getItem(), color);
        return true;
    }
}
