package com.lootbeams.config.impl;

import com.lootbeams.events.ConfigReloadEvent;
import net.neoforged.bus.api.SubscribeEvent;

public interface IConfigReloadable {

    void onReload(ConfigReloadEvent event);
}
