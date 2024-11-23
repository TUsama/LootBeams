package com.lootbeams;

import com.lootbeams.modules.ModulesManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = LootBeams.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class LootBeamsClient {
    @SubscribeEvent
    public static void onClientSetUp(FMLClientSetupEvent event){
        event.enqueueWork(ModulesManager::registerAll);
    }
}
