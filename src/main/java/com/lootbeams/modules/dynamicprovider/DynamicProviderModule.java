package com.lootbeams.modules.dynamicprovider;

import com.lootbeams.LootBeams;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.config.impl.IConfigReloadable;
import com.lootbeams.events.ConfigReloadEvent;
import com.lootbeams.modules.ILBModule;
import io.vavr.control.Option;
import net.neoforged.bus.api.SubscribeEvent;

public class DynamicProviderModule implements ILBModule, IConfigReloadable {

    public final static DynamicProviderModule INSTANCE = new DynamicProviderModule();

    private DynamicProvider dynamicProvider;

    public DynamicProviderModule() {
    }

    @Override
    public void tryEnable() {
        if (ConfigurationManager.request(Config.ENABLE_DYNAMIC_PROVIDER)) {
            //only initialize the dynamicProvider when enabled.
            this.dynamicProvider = new DynamicProvider(ConfigurationManager.request(Config.HALF_ROUND_TICKS));
            LootBeams.EVENT_BUS.register(this.dynamicProvider);

        }
    }

    public static Option<DynamicProvider> getDynamicProvider() {
        //System.out.println(INSTANCE.dynamicProvider == null);
        return Option.of(INSTANCE.dynamicProvider);
    }

    @Override
    @SubscribeEvent
    public void onReload(ConfigReloadEvent event) {
        LootBeams.EVENT_BUS.unregister(this.dynamicProvider);
        this.dynamicProvider = new DynamicProvider(ConfigurationManager.request(Config.HALF_ROUND_TICKS));
        LootBeams.EVENT_BUS.register(this.dynamicProvider);
    }
}
