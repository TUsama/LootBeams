package com.lootbeams.modules.compat.apothesis;

import com.lootbeams.LootBeams;
import com.lootbeams.modules.compat.ILBCompatModule;
import com.lootbeams.events.LBEventBus;
import com.lootbeams.events.RegisterLBRarityEvent;
import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraftforge.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.logging.Logger;

public class ApotheosisCompatModule implements ILBCompatModule {
    @Override
    public boolean shouldBeEnable() {
        return ModList.get().isLoaded(Apotheosis.MODID);
    }

    @Override
    public void tryEnable() {
        if (shouldBeEnable()){
            LootBeams.LOGGER.info("Detected Apotheosis, enable compat module!");
            LBEventBus.INSTANCE.register(new ApotheosisCompatModule());
        }
    }

    @SubscribeEvent
    public void onEnable(RegisterLBRarityEvent.Pre event){
        event.register(new ApotheosisLootRarity.Accessor().getApplier());
    }
}
