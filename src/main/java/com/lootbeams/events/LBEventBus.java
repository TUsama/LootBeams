package com.lootbeams.events;

import com.google.common.eventbus.EventBus;

public class LBEventBus extends EventBus {

    public static final LBEventBus INSTANCE = new LBEventBus("lootbeams");

    public LBEventBus(String identifier) {
        super(identifier);
    }
}
