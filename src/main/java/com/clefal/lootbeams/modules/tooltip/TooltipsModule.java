package com.clefal.lootbeams.modules.tooltip;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.config.Config;
import com.clefal.lootbeams.config.ConfigurationManager;
import com.clefal.lootbeams.config.impl.TooltipsEnableStatus;
import com.clefal.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.clefal.lootbeams.modules.ILBModule;
import com.clefal.lootbeams.modules.tooltip.nametag.NameTagRenderer;
import com.clefal.lootbeams.modules.tooltip.overlay.AdvanceTooltipOverlay;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TooltipsModule implements ILBModule {

    public final static TooltipsModule INSTANCE = new TooltipsModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderNameTag(EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent event) {
        if (ConfigurationManager.request(Config.ENABLE_TOOLTIPS) == TooltipsEnableStatus.TooltipsStatus.NAME_RARITY_TOOLTIPS) return;
        NameTagRenderer.renderNameTag(event.poseStack, event.buffers, event.LBItemEntity);
    }



    @Override
    public void tryEnable() {
        FMLJavaModLoadingContext.get().getModEventBus().register(INSTANCE);
        LootBeams.EVENT_BUS.register(INSTANCE);
    }
}
