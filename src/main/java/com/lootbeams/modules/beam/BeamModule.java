package com.lootbeams.modules.beam;

import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.events.LBEventBus;
import com.lootbeams.modules.ILBModule;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class BeamModule implements ILBModule {

    public static final BeamModule INSTANCE = new BeamModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEnableModule(EntityRenderDispatcherHookEvent.RenderLootBeamEvent event){
        BeamRenderer.renderLootBeam(event.poseStack, event.buffers, event.partialTicks, event.entity.level().getGameTime(), event.entity, Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
    }


    @Override
    public void tryEnable() {
        if (ConfigurationManager.request(Config.ENABLE_BEAM)){
            LBEventBus.INSTANCE.register(INSTANCE);
        }
    }
}
