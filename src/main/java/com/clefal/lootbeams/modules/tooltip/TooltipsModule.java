package com.clefal.lootbeams.modules.tooltip;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.clefal.lootbeams.modules.ILBModule;
import com.clefal.lootbeams.modules.tooltip.nametag.NameTagRenderer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TooltipsModule implements ILBModule {

    public final static TooltipsModule INSTANCE = new TooltipsModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderTooltips(EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent event) {
        NameTagRenderer.renderNameTag(event.poseStack, event.buffers, event.LBItemEntity);
    }

    @Override
    public void tryEnable() {

        LootBeams.EVENT_BUS.register(INSTANCE);

    }
}
