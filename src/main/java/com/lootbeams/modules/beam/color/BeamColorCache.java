package com.lootbeams.modules.beam.color;

import com.lootbeams.modules.ILBModuleRenderCache;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class BeamColorCache implements ILBModuleRenderCache<BeamColorSourceContainer, ItemEntity> {
    private final static WeakHashMap<ItemStack, Color> colorMap = new WeakHashMap<>(500);
    private final static BeamColorCache INSTANCE = new BeamColorCache();
    private final static Object lock = new Object();

    private static boolean mark = false;

    public static Either<Boolean, Color> ask(ItemEntity entity){
        ItemStack item = entity.getItem();

        if (colorMap.containsKey(item)) {

            return Either.right(colorMap.get(item));
        }
        INSTANCE.asyncHandle(BeamColorSourceContainer.INSTANCE, entity, mark);
        return Either.left(false);
    }

    protected static boolean provide(ItemEntity entity, Color color){
        if (colorMap.containsKey(entity.getItem())){
            return false;
        }
        synchronized (lock){
            colorMap.put(entity.getItem(), color);
        }
        return true;
    }

    @Override
    public BiConsumer<BeamColorSourceContainer, ItemEntity> getDataHandler() {
        return ((beamColorSourceContainer, itemEntity) -> {
            Color itemColor = BeamColorSourceContainer.getItemColor(itemEntity);
            provide(itemEntity, itemColor);
            mark = false;
        });
    }
}
