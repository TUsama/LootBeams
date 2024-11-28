package com.lootbeams.data;

import com.lootbeams.data.rarity.LBRarity;
import net.minecraft.world.entity.item.ItemEntity;

public record LBItemEntity(ItemEntity item, LBRarity rarity, boolean isSounded) {

    public static LBItemEntity of(ItemEntity item, LBRarity rarity) {
        return new LBItemEntity(item, rarity, false);
    }

    public LBItemEntity to(LBRarity rarity) {
        return new LBItemEntity(item, rarity, isSounded);
    }

    public LBItemEntity sounded() {
        return new LBItemEntity(item, rarity, true);
    }
}
