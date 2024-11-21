package com.lootbeams.events;

import net.minecraftforge.eventbus.BusBuilderImpl;
import net.minecraftforge.eventbus.EventBus;

public class LBEventBus extends EventBus {

    public static final LBEventBus INSTANCE = new LBEventBus(new BusBuilderImpl());

    public LBEventBus(BusBuilderImpl busBuilder) {
        super(busBuilder);
    }
}
