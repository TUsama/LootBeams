package com.lootbeams.modules.rarity.impl;

import com.lootbeams.modules.rarity.ILBRarity;
import net.minecraft.world.entity.item.ItemEntity;

import java.awt.*;

/**
 * Indicated a ILBRarity that serve as a pure color and name provider. This class cannot be added to LBRarityContainer.
 */
public class LBOverridedRarity implements ILBRarity {

    private Color color;

    private String name;

    public LBOverridedRarity(Color color, String name) {
        this.color = color;
        this.name = name;
    }
    public static LBOverridedRarity from(ILBRarity rarity){
        return new LBOverridedRarity(rarity.getColor(), rarity.getName());
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public boolean isThisRarity(ItemEntity itemEntity) {
        return false;
    }


}
