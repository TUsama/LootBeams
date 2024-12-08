package com.clefal.lootbeams.modules.compat.mine_and_slash;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.data.LBItemEntity;
import com.clefal.lootbeams.data.rarity.LBRarity;
import com.clefal.lootbeams.events.RegisterLBRarityEvent;
import com.clefal.lootbeams.modules.compat.ILBCompatModule;
import com.robertx22.mine_and_slash.database.data.gear_slots.GearSlot;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import io.vavr.control.Option;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.vavr.API.*;

public class MineAndSlashCompatModule implements ILBCompatModule {

    private final static List<String> rarities = new ArrayList<>();


    @Override
    public boolean shouldBeEnable() {
        return ModList.get().isLoaded(SlashRef.MODID);
    }

    @Override
    public void tryEnable() {
        if (shouldBeEnable()) {
            LootBeams.LOGGER.info("Detected Mine and Slash, enable MineAndSlashCompatModule!");
            LootBeams.EVENT_BUS.register(new MineAndSlashCompatModule());
            rarities.add("common");
            rarities.add("uncommon");
            rarities.add("rare");
            rarities.add("epic");
            rarities.add("legendary");
            rarities.add("mythic");
            rarities.add("unique");
            rarities.add("runeword");

        }
    }

    @SubscribeEvent
    public void onEnable(RegisterLBRarityEvent.Pre event) {
        event.register(itemEntity ->

                Match(Match(itemEntity.getItem()).option(
                        //ItemEntity -> GearRarity
                        Case($(stack -> StackSaving.GEARS.has(stack)), stack -> StackSaving.GEARS.loadFrom(stack).getRarity())
                )).option(
                        //GearRarity -> LBRarity
                        Case($(option -> !option.isEmpty()), option -> LBItemEntity.of(itemEntity, LBRarity.of(
                                option.get().locName(),
                                new Color(option.get().textFormatting().getColor()),
                                rarities.indexOf(option.get().guid)
                        ))),
                        Case($(option -> GearSlot.getSlotOf(itemEntity.getItem()) != null), option -> LBItemEntity.of(itemEntity, getNonSoulRarity()))
                ));
    }

    public static LBRarity getNonSoulRarity(){
        return new LBRarity(Component.translatable("lootbeams.mod_rarity.non_soul"), new Color(121, 121, 121), -1);
    }
}
