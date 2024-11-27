package com.lootbeams.modules.rarity;

import io.vavr.Function1;
import io.vavr.control.Option;
import net.minecraft.world.entity.item.ItemEntity;

@FunctionalInterface
public interface ILBRarityApplier extends Function1<ItemEntity, Option<ItemWithRarity>> {

}
