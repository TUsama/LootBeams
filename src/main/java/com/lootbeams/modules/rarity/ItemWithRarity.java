package com.lootbeams.modules.rarity;

import net.minecraft.world.entity.item.ItemEntity;

public record ItemWithRarity(ItemEntity item, LBRarity rarity) {

    public static ItemWithRarity of(ItemEntity item, LBRarity rarity) {
        return new ItemWithRarity(item, rarity);
    }

    public ItemWithRarity to(LBRarity rarity) {
        return new ItemWithRarity(item, rarity);
    }
}
