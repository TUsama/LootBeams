package com.lootbeams.modules.rarity;

import net.minecraft.world.entity.item.ItemEntity;

public record ItemWithRarity(ItemEntity item, ILBRarity rarity) {

    public static ItemWithRarity of(ItemEntity item, ILBRarity rarity) {
        return new ItemWithRarity(item, rarity);
    }

    public ItemWithRarity to(ILBRarity rarity) {
        return new ItemWithRarity(item, rarity);
    }
}
