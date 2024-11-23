package com.lootbeams.modules.tooltip;

import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.events.LBEventBus;
import com.lootbeams.modules.ILBModule;
import com.lootbeams.modules.beam.BeamRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TooltipsModule implements ILBModule {

    public final static TooltipsModule INSTANCE = new TooltipsModule();

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void renderTooltips(EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent event){
        TooltipRenderer.renderNameTag(event.poseStack, event.buffers, event.entity, Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
    }

    @Override
    public void tryEnable() {
        if (ConfigurationManager.request(Config.ENABLE_TOOLTIPS)){
            LBEventBus.INSTANCE.register(INSTANCE);
        }
    }
}
