package com.lootbeams.modules.rarity.impl;

import com.lootbeams.modules.rarity.ILBRarity;
import com.lootbeams.modules.rarity.ILBRarityApplier;
import com.lootbeams.modules.rarity.ItemWithRarity;
import io.vavr.control.Option;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import org.apache.commons.lang3.StringUtils;

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
        return StringUtils.capitalize(rarity.name().toLowerCase());
    }

    @Override
    public Color getColor() {
        var i = this.rarity.getStyleModifier().apply(Style.EMPTY).getColor().getValue();
        return new Color(i >> 16 & 255, i >> 8 & 255, i & 255);
    }



    @Override
    public ILBRarityApplier getApplier() {
        return itemEntity1 -> {
            if (itemEntity1.getItem().getRarity().equals(this.rarity)) {
                return Option.some(ItemWithRarity.of(itemEntity1, this));
            } else {
                return Option.none();
            }
        };
    }
}
