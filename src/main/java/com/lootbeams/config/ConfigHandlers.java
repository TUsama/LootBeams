package com.lootbeams.config;

import com.lootbeams.LootBeams;
import com.lootbeams.config.impl.IConfigReloadable;
import com.lootbeams.config.impl.ModifyingConfigHandler;
import com.lootbeams.data.rarity.ConfigColorOverride;
import com.lootbeams.events.ConfigReloadEvent;
import lombok.Getter;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

public class ConfigHandlers implements IConfigReloadable {
    public static ConfigHandlers INSTANCE = new ConfigHandlers();
    @Getter
    private List<ModifyingConfigHandler> handlers;

    public ConfigHandlers() {
        registerAll();

    }

    public static void init(){
        LootBeams.EVENT_BUS.register(INSTANCE);
    }


    public void registerAll(){
        this.handlers = List.of(
                new ConfigColorOverride()
        );
    }

    @SubscribeEvent
    @Override
    public void onReload(ConfigReloadEvent event){
        INSTANCE.handlers.clear();
        INSTANCE.registerAll();
    }
}
