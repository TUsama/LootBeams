package com.lootbeams.modules.tooltip;

import com.lootbeams.events.RenderLBTooltipsEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class TooltipsListener {

    @SubscribeEvent(receiveCanceled = true)
    public static void renderTooltips(RenderLBTooltipsEvent event){

    }
}
