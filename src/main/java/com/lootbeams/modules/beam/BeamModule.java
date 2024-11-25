package com.lootbeams.modules.beam;

import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.events.LBEventBus;
import com.lootbeams.modules.ILBModule;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class BeamModule implements ILBModule {

    public static final BeamModule INSTANCE = new BeamModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEnableModule(EntityRenderDispatcherHookEvent.RenderLootBeamEvent event) {
        BeamRenderer.renderLootBeam(event.poseStack, event.buffers, event.partialTicks, event.itemWithRarity, Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
    }


    @Override
    public void tryEnable() {
        LBEventBus.INSTANCE.register(INSTANCE);
    }
}
