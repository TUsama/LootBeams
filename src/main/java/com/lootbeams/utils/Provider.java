package com.lootbeams.utils;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class Provider {


    public static String getRarity(ItemStack stack) {
        String rarity = stack.getRarity().name().toLowerCase();
        /*if (ModList.get().isLoaded("apotheosis")) {
            if (ApotheosisCompat.isApotheosisItem(stack)) {
                rarity = ApotheosisCompat.getRarityName(stack);
            }
        }*/
        rarity = rarity.replace(":", ".").replace("_", ".");
        if (I18n.exists(rarity)) {
            return I18n.get(rarity);
        }
        return rarity;
    }

    /**
     * Gets color from the first letter in the text component.
     */
    @Nonnull
    public static Color getNameColor(Component name) {
        List<Style> list = Lists.newArrayList();
        name.visit((style, string) -> {
            StringDecomposer.iterateFormatted(string, style, (pPositionInCurrentSequence, style1, pCodePoint) -> {
                list.add(style);
                return true;
            });
            return Optional.empty();
        }, Style.EMPTY);
        if (list.get(0).getColor() != null) {
            return new Color(list.get(0).getColor().getValue());
        }
        return Color.WHITE;
    }
}
