package com.lootbeams.modules.rarity.impl;

import com.lootbeams.modules.rarity.ILBRarity;
import net.minecraft.world.entity.item.ItemEntity;

import java.awt.*;

/**
 * Indicated a ILBRarity that serve as a pure color and name provider. This class cannot be added to LBRarityContainer.
 */
public class LBOverridableRarity implements ILBRarity {

    private Color color;

    private String name;

    public LBOverridableRarity(Color color, String name) {
        this.color = color;
        this.name = name;
    }
    public static LBOverridableRarity from(ILBRarity rarity){
        return new LBOverridableRarity(rarity.getColor(), rarity.getName());
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
