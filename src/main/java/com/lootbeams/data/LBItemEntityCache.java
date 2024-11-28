package com.lootbeams.data;

import com.lootbeams.data.rarity.ConfigColorOverride;
import com.lootbeams.data.rarity.LBRarityContainer;
import com.lootbeams.modules.ILBModuleRenderCache;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class LBItemEntityCache implements ILBModuleRenderCache<LBRarityContainer, ItemEntity> {
    private final static ConcurrentHashMap<ItemStack, SoftReference<LBItemEntity>> rarityMap = new ConcurrentHashMap<>(500);
    private final static LBItemEntityCache INSTANCE = new LBItemEntityCache();
    private final static Object lock = new Object();

    private static boolean mark = false;

    public static SoftReference<LBItemEntity> ask(ItemEntity entity) {
        ItemStack item = entity.getItem();

        if (rarityMap.containsKey(item)) {
            if (rarityMap.get(item).get() == null) {
                INSTANCE.handle(LBRarityContainer.INSTANCE, entity, mark);
            }
            return rarityMap.get(item);
        }
        INSTANCE.handle(LBRarityContainer.INSTANCE, entity, mark);
        System.out.println(rarityMap.size());
        return rarityMap.get(item);
    }

    public static void updateSoundStatus(ItemStack itemStack){
        LBItemEntity lbItemEntity = rarityMap.get(itemStack).get();
        if (lbItemEntity != null){
            LBItemEntity sounded = lbItemEntity.sounded();
            rarityMap.put(itemStack, new SoftReference<>(sounded));
        }
    }

    protected static boolean provide(ItemEntity entity, SoftReference<LBItemEntity> itemWithRarity) {
        if (rarityMap.containsKey(entity.getItem())) {
            return false;
        }
        rarityMap.put(entity.getItem(), itemWithRarity);
        return true;
    }

    @Override
    public BiConsumer<LBRarityContainer, ItemEntity> getDataHandler() {
        return ((lbRarityContainer, itemEntity) -> {
            LBItemEntity itemRarity = LBRarityContainer.getItemWithRarity(itemEntity);
            LBItemEntity LBItemEntity = ConfigColorOverride.tryGetConfigRarity(itemRarity);
            provide(itemEntity, new SoftReference<>(LBItemEntity));
            System.out.println("put new one!");
            mark = false;
        });
    }
}
