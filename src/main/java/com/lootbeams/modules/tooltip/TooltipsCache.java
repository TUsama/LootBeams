package com.lootbeams.modules.tooltip;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.WeakHashMap;

public class TooltipsCache {

    private final static WeakHashMap<ItemStack, List<Component>> textMap = new WeakHashMap<>(500);
    private final static Object lock = new Object();

    public static Either<Boolean, List<Component>> ask(ItemEntity entity) {
        ItemStack item = entity.getItem();

        if (textMap.containsKey(item)) {

            return Either.right(textMap.get(item));
        }
        List<Component> texts = entity.getItem().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);

        provide(entity, texts);
        return Either.right(texts);
    }

    protected static boolean provide(ItemEntity entity, List<Component> components) {
        if (textMap.containsKey(entity.getItem())) {
            return false;
        }
        synchronized (lock) {
            textMap.put(entity.getItem(), components);
        }
        return true;
    }
}
