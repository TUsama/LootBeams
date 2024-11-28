package com.lootbeams.modules.compat.mine_and_slash;

import com.lootbeams.LootBeams;
import com.lootbeams.events.RegisterLBRarityEvent;
import com.lootbeams.modules.compat.ILBCompatModule;
import com.lootbeams.data.LBItemEntity;
import com.lootbeams.data.rarity.LBRarity;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarity;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.VanillaRarities;
import io.vavr.control.Option;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.vavr.API.*;

public class MineAndSlashCompoaModule implements ILBCompatModule {

    private final static List<String> rarities = new ArrayList<>();


    @Override
    public boolean shouldBeEnable() {
        return ModList.get().isLoaded(SlashRef.MODID);
    }

    @Override
    public void tryEnable() {
        if (shouldBeEnable()) {
            LootBeams.LOGGER.info("Detected Mine and Slash, enable compat module!");
            LootBeams.EVENT_BUS.register(new MineAndSlashCompoaModule());
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

                Match(Match(itemEntity.getItem()).of(
                        //ItemEntity -> GearRarity
                Case($(stack -> StackSaving.GEARS.has(stack)), stack -> Option.some(StackSaving.GEARS.loadFrom(stack).getRarity())),
                Case($(), stack -> Option.<GearRarity>none())
        )).of(
                //GearRarity -> LBRarity
                Case($(option -> !option.isEmpty()), option -> Option.some(LBItemEntity.of(itemEntity, LBRarity.of(
                        option.get().locName(),
                        new Color(option.get().textFormatting().getColor()),
                        rarities.indexOf(option.get().guid)
                )))),
                Case($(), option -> Option.none())
        ));
    }
}
