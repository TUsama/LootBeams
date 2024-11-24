package com.lootbeams.modules.tooltip;

import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.events.LBEventBus;
import com.lootbeams.modules.ILBModule;
import com.lootbeams.modules.tooltip.nametag.NameTagRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TooltipsModule implements ILBModule {

    public final static TooltipsModule INSTANCE = new TooltipsModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderTooltips(EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent event) {
        NameTagRenderer.renderNameTag(event.poseStack, event.buffers, event.entity, Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
    }

    @Override
    public void tryEnable() {

        LBEventBus.INSTANCE.register(INSTANCE);

    }
}
