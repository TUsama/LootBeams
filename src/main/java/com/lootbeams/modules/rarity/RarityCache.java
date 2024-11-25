package com.lootbeams.modules.rarity;

import com.lootbeams.modules.ILBModuleRenderCache;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class RarityCache implements ILBModuleRenderCache<LBRarityContainer, ItemEntity> {
    private final static ConcurrentHashMap<ItemStack, SoftReference<ItemWithRarity>> rarityMap = new ConcurrentHashMap<>(500);
    private final static RarityCache INSTANCE = new RarityCache();
    private final static Object lock = new Object();

    private static boolean mark = false;

    public static SoftReference<ItemWithRarity> ask(ItemEntity entity) {
        ItemStack item = entity.getItem();

        if (rarityMap.containsKey(item)) {
            if (rarityMap.get(item).get() == null) {
                INSTANCE.handle(LBRarityContainer.INSTANCE, entity, mark);
            }
            return rarityMap.get(item);
        }
        INSTANCE.handle(LBRarityContainer.INSTANCE, entity, mark);

        return rarityMap.get(item);
    }

    protected static boolean provide(ItemEntity entity, SoftReference<ItemWithRarity> itemWithRarity) {
        if (rarityMap.containsKey(entity.getItem())) {
            return false;
        }
        rarityMap.put(entity.getItem(), itemWithRarity);
        return true;
    }

    @Override
    public BiConsumer<LBRarityContainer, ItemEntity> getDataHandler() {
        return ((lbRarityContainer, itemEntity) -> {
            ILBRarity itemRarity = LBRarityContainer.getItemRarity(itemEntity);
            ItemWithRarity itemWithRarity = ConfigColorOverride.tryGetConfigRarity(ItemWithRarity.of(itemEntity, itemRarity));
            provide(itemEntity, new SoftReference<>(itemWithRarity));
            mark = false;
        });
    }
}
