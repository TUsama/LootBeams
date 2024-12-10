package com.clefal.lootbeams.modules.compat.obscuretooltips;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.events.TooltipsGatherNameAndRarityEvent;
import com.clefal.lootbeams.modules.compat.ILBCompatModule;
import com.obscuria.tooltips.ObscureTooltips;
import net.minecraftforge.fml.ModList;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class ObscureTooltipsCompatModule implements ILBCompatModule {
    public final static ObscureTooltipsCompatModule INSTANCE = new ObscureTooltipsCompatModule();

    @SubscribeEvent(priority = EventPriority.LOW)
    public void removeRarity(TooltipsGatherNameAndRarityEvent event) {
        event.gather.remove(TooltipsGatherNameAndRarityEvent.Case.RARITY);
    }

    @Override
    public boolean shouldBeEnable() {
        return ModList.get().isLoaded(ObscureTooltips.MODID);
    }

    @Override
    public void tryEnable() {
        if (shouldBeEnable()){
            LootBeams.LOGGER.info("Detected Obscure Tooltips, enable ObscureTooltipsCompatModule!");
            LootBeams.EVENT_BUS.register(INSTANCE);
        }
    }
}
