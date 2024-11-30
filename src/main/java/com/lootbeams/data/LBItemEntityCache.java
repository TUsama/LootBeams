package com.lootbeams.data;

import com.lootbeams.config.ConfigHandlers;
import com.lootbeams.config.impl.ModifyingConfigHandler;
import com.lootbeams.modules.ILBModuleRenderCache;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class LBItemEntityCache implements ILBModuleRenderCache<InternalLBItemEntityProvider, ItemEntity> {
    private final static ConcurrentHashMap<ItemStack, SoftReference<LBItemEntity>> rarityMap = new ConcurrentHashMap<>(500);
    private final static LBItemEntityCache INSTANCE = new LBItemEntityCache();
    private final static Object lock = new Object();

    private static boolean mark = false;

    public static SoftReference<LBItemEntity> ask(ItemEntity entity) {
        ItemStack item = entity.getItem();

        if (rarityMap.containsKey(item)) {
            if (rarityMap.get(item).get() == null) {
                INSTANCE.handle(InternalLBItemEntityProvider.INSTANCE, entity, mark);
            }
            return rarityMap.get(item);
        }
        INSTANCE.handle(InternalLBItemEntityProvider.INSTANCE, entity, mark);
        System.out.println(rarityMap.size());
        return rarityMap.get(item);
    }



    protected static boolean provide(ItemEntity entity, SoftReference<LBItemEntity> itemWithRarity) {
        if (rarityMap.containsKey(entity.getItem())) {
            return false;
        }
        rarityMap.put(entity.getItem(), itemWithRarity);
        return true;
    }

    @Override
    public BiConsumer<InternalLBItemEntityProvider, ItemEntity> getDataHandler() {
        return ((internalLbItemEntityProvider, itemEntity) -> {
            LBItemEntity lbItemEntity = InternalLBItemEntityProvider.getLBItemEntity(itemEntity);
            //override by config
            for (ModifyingConfigHandler handler : ConfigHandlers.INSTANCE.getHandlers()) {
                lbItemEntity = handler.modify(lbItemEntity);
            }

            provide(itemEntity, new SoftReference<>(lbItemEntity));
            mark = false;
        });
    }
}
