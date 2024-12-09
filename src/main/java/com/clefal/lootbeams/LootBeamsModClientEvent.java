package com.clefal.lootbeams;

import com.clefal.lootbeams.config.ConfigHandlers;
import com.clefal.lootbeams.modules.ModulesManager;
import com.clefal.lootbeams.modules.tooltip.overlay.AdvanceTooltipOverlay;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = LootBeams.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LootBeamsModClientEvent {
    public static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(LootBeams.MODID, "glow");

    public static ShaderInstance PARTICLE_ADDITIVE_MULTIPLY;

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CHAT_PANEL.id(), LootBeams.MODID + "lb_tooltips", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {

            AdvanceTooltipOverlay.INSTANCE.render(guiGraphics, partialTick, screenWidth, screenHeight);
        });
    }


    @SubscribeEvent
    public static void registerModules(FMLClientSetupEvent event) {
        LootBeams.LOGGER.info("register all modules");
        ModulesManager.registerAll();
        ConfigHandlers.init();
    }

}
