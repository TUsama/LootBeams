package com.lootbeams.modules.compat.mine_and_slash;

import com.lootbeams.LootBeams;
import com.lootbeams.events.RegisterLBRarityEvent;
import com.lootbeams.modules.compat.ILBCompatModule;
import com.lootbeams.modules.rarity.ItemWithRarity;
import com.lootbeams.modules.rarity.LBRarity;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarity;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import io.vavr.control.Option;
import net.minecraftforge.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;

import java.awt.*;

import static io.vavr.API.*;

public class MineAndSlashCompoaModule implements ILBCompatModule {


    @Override
    public boolean shouldBeEnable() {
        return ModList.get().isLoaded(SlashRef.MODID);
    }

    @Override
    public void tryEnable() {
        if (shouldBeEnable()) {
            LootBeams.LOGGER.info("Detected Mine and Slash, enable compat module!");
            LootBeams.EVENT_BUS.register(new MineAndSlashCompoaModule());
        }
    }

    @SubscribeEvent
    public void onEnable(RegisterLBRarityEvent.Pre event) {
        event.register(itemEntity ->
                Match(Match(itemEntity.getItem()).of(
                Case($(stack -> StackSaving.GEARS.has(stack)), stack -> Option.some(StackSaving.GEARS.loadFrom(stack).getRarity())),
                Case($(), stack -> Option.<GearRarity>none())
        )).of(
                Case($(option -> !option.isEmpty()), option -> Option.some(ItemWithRarity.of(itemEntity, LBRarity.of(option.get().locName(), new Color(option.get().textFormatting().getColor()))))),
                Case($(), option -> Option.none())
        ));
    }
}
