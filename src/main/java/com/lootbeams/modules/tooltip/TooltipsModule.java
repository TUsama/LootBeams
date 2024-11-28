package com.lootbeams.modules.tooltip;

import com.lootbeams.LootBeams;
import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.modules.ILBModule;
import com.lootbeams.modules.tooltip.nametag.NameTagRenderer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TooltipsModule implements ILBModule {

    public final static TooltipsModule INSTANCE = new TooltipsModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderTooltips(EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent event) {
        NameTagRenderer.renderNameTag(event.poseStack, event.buffers, event.itemWithRarity);
    }

    @Override
    public void tryEnable() {

        LootBeams.EVENT_BUS.register(INSTANCE);

    }
}
