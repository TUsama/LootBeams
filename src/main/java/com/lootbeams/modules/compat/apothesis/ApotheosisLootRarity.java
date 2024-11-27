package com.lootbeams.modules.compat.apothesis;

import com.lootbeams.modules.rarity.ILBRarity;
import com.lootbeams.modules.rarity.ILBRarityApplier;
import com.lootbeams.modules.rarity.ItemWithRarity;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemItem;
import io.vavr.control.Option;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

import static io.vavr.API.*;

public abstract class ApotheosisLootRarity implements ILBRarity {




    @Override
    public ILBRarityApplier getApplier() {
        return itemEntity -> {
            var stack = itemEntity.getItem();
            return Match(Match(stack).of(
                    //ItemStack -> LootRarity
                    Case($(v -> AffixHelper.hasAffixes(v) || v.getItem() instanceof GemItem), AffixHelper::getRarity),
                    Case($(v -> (v.getItem() instanceof SalvageItem)), v -> RarityRegistry.getMaterialRarity(v.getItem())),
                    Case($(), v -> RarityRegistry.INSTANCE.emptyHolder())
            )).of(
                    //LootRarity -> ILBRarity
                    Case($(v -> !v.is(RarityRegistry.INSTANCE.emptyHolder().getId())), v -> Option.none()),
                    Case($(v -> !v.isBound()), v -> Option.none()),
                    Case($(), v -> Option.some(ItemWithRarity.of(itemEntity, new delegate(v.get())))
            ));
        };
    }


    private class delegate extends ApotheosisLootRarity{

        LootRarity rarity;

        public delegate(LootRarity rarity) {
            this.rarity = rarity;
        }

        @Override
        public String getName() {
            return StringUtils.capitalize(rarity.toComponent().getString().toLowerCase());
        }

        @Override
        public Color getColor() {
            return new Color(rarity.getColor().getValue());
        }
    }

    public static class Accessor extends ApotheosisLootRarity{

        @Override
        public String getName() {
            return "";
        }

        @Override
        public Color getColor() {
            return null;
        }
    }
}
