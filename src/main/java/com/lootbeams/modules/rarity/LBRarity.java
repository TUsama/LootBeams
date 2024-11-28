package com.lootbeams.modules.rarity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public record LBRarity(Component name, Color color) {
    public final static String vanillaRarityKeFormat = "lootbeams.vanilla_rarity.";

    public static LBRarity of(Component name, Color color) {
        return new LBRarity(name, color);
    }

    public static LBRarity of(Rarity rarity){
        Component translatable = Component.translatable(vanillaRarityKeFormat + rarity.name().toLowerCase());
        return new LBRarity(translatable, new Color(rarity.getStyleModifier().apply(Style.EMPTY).getColor().getValue()));
    }
}
