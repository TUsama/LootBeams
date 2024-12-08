package com.clefal.lootbeams.config.impl;

import com.clefal.lootbeams.config.Config;
import com.clefal.lootbeams.config.ConfigurationManager;
import com.clefal.lootbeams.data.LBItemEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.vavr.Function1;
import io.vavr.collection.Vector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Map;

public class TooltipsEnableStatus {
    public static final String NAME = "name";
    public static final String RARITY = "rarity";
    public static final String TOOLTIPS = "tooltips";
    public static final Function1<LBItemEntity, Component> handleName = lbItemEntity -> {
        Boolean ifShowStack = ConfigurationManager.<Boolean>request(Config.RENDER_STACKCOUNT);
        ItemStack item = lbItemEntity.item().getItem();
        if (!ifShowStack) return item.getHoverName();
        int count = item.getCount();
        if (count > 1){
            return item.getHoverName().plainCopy().append(" x" + count);
        }
        return item.getHoverName();
    };

    public enum TooltipsStatus {
        NONE(lbItemEntity -> {
            throw new UnsupportedOperationException("can't extract Components on NONE status!");
        }),
        ONLY_NAME(lbItemEntity -> ImmutableMap.of(
                NAME, Vector.of(handleName.apply(lbItemEntity))
        )),
        NAME_AND_RARITY(lbItemEntity ->
                ImmutableMap.of(
                        NAME, Vector.of(handleName.apply(lbItemEntity)),
                        RARITY, Vector.of(lbItemEntity.rarity().name())
                )),
        NAME_RARITY_TOOLTIPS(lbItemEntity -> ImmutableMap.of(
                NAME, Vector.of(handleName.apply(lbItemEntity)),
                RARITY, Vector.of(lbItemEntity.rarity().name()),
                TOOLTIPS, Vector.ofAll(lbItemEntity.item().getItem().getTooltipLines(null, TooltipFlag.Default.NORMAL))
        ));


        public final Function1<LBItemEntity, Map<String, Vector<Component>>> extractComponents;

        TooltipsStatus(Function1<LBItemEntity, Map<String, Vector<Component>>> extractComponents) {
            this.extractComponents = extractComponents;
        }


        public static List<Component> safeGetNameAndRarity(LBItemEntity lbItemEntity){
            TooltipsStatus status = ConfigurationManager.request(Config.ENABLE_TOOLTIPS);
            Map<String, Vector<Component>> map = status.extractComponents.apply(lbItemEntity);
            return switch (status) {
                case ONLY_NAME -> ImmutableList.of(map.get(NAME).get());
                case NAME_AND_RARITY,NAME_RARITY_TOOLTIPS -> ImmutableList.of(map.get(NAME).get(),
                        map.get(RARITY).get());
                default -> ImmutableList.of();
            };
        }

        public static Vector<Component> safeGetTooltips(LBItemEntity lbItemEntity){
            TooltipsStatus status = ConfigurationManager.request(Config.ENABLE_TOOLTIPS);
            Map<String, Vector<Component>> map = status.extractComponents.apply(lbItemEntity);
            return switch (status) {
                case NAME_RARITY_TOOLTIPS -> map.get(TOOLTIPS);
                default -> Vector.of();
            };
        }
    }
}
