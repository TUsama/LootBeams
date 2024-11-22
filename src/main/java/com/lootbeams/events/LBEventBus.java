package com.lootbeams.events;


import net.neoforged.bus.BusBuilderImpl;
import net.neoforged.bus.EventBus;

public class LBEventBus extends EventBus {

    public static final LBEventBus INSTANCE = new LBEventBus(new BusBuilderImpl());

    public LBEventBus(BusBuilderImpl busBuilder) {
        super(busBuilder);
    }
}
