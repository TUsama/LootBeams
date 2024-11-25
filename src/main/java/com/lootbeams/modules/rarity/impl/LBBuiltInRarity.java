package com.lootbeams.modules.rarity.impl;

import com.lootbeams.modules.rarity.ILBRarity;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Rarity;

import java.awt.*;
//It's not a good idea to change the vanilla rarity color.
//So here I just let this class implement the ILBImmutableColorRarity.
//It can also compat with other mods that change the vanilla rarity color.
public enum LBBuiltInRarity implements ILBRarity {
    COMMON(Rarity.COMMON),
    UNCOMMON(Rarity.UNCOMMON),
    RARE(Rarity.RARE),
    EPIC(Rarity.EPIC);



    final Rarity rarity;
    LBBuiltInRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public Color getColor() {
        var i = this.rarity.getStyleModifier().apply(Style.EMPTY).getColor().getValue();
        return new Color(i >> 16 & 255, i >> 8 & 255, i & 255);
    }



    @Override
    public boolean isThisRarity(ItemEntity itemEntity) {
        return itemEntity.getItem().getRarity().equals(this.rarity);
    }
}
