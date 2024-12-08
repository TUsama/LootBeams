package com.clefal.lootbeams.data.rarity;

import com.clefal.lootbeams.config.Config;
import com.clefal.lootbeams.config.ConfigurationManager;
import com.clefal.lootbeams.config.impl.ModifyingConfigHandler;
import com.clefal.lootbeams.data.LBItemEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ConfigCustomRarity extends ModifyingConfigHandler {
    public final static String langKeyFormat = "lootbeams.fake_rarity.";
    private final Set<ResourceLocation> customRarity;


    public ConfigCustomRarity() {
        this.customRarity = ConfigurationManager.<List<String>>request(Config.CUSTOM_RARITIES)
                .stream()
                .filter(x -> !x.contains("#"))
                .map(x -> x.replace("#", ""))
                .map(ResourceLocation::tryParse)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public LBItemEntity modify(LBItemEntity lbItemEntity) {
        if (customRarity.isEmpty()) return lbItemEntity;
        ItemStack item = lbItemEntity.item().getItem();
        //request can't be Config.TooltipsStatus.NONE here,
        //because the RenderLBTooltipsEvent will not be fire if request is Config.TooltipsStatus.NONE


        AtomicReference<LBItemEntity> newEntity = new AtomicReference<>();
        item.getTags()
                .map(x -> Pair.of(customRarity.contains(x.location()), x.location()))
                .filter(Pair::getFirst)
                .findFirst()
                .ifPresentOrElse(x -> {
                    //provide custom rarity
                    ResourceLocation Location = x.getSecond();
                    String rarity = Location.getPath();
                    MutableComponent newName = Component.literal(I18n.exists(langKeyFormat + rarity) ? I18n.get(langKeyFormat + rarity) : rarity);
                    LBRarity old = lbItemEntity.rarity();
                    //todo all custom rarities' ordinal is 4, could this be changed?
                    newEntity.set(lbItemEntity.to(LBRarity.of(newName, old.color(), 4)));

                }, () -> {
                    //if this item doesn't have a custom rarity, use the built-in rarity checker instead.
                    newEntity.set(lbItemEntity);
                });

        return newEntity.get();
    }
}
