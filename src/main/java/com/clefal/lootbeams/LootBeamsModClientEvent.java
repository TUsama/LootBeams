package com.clefal.lootbeams;

import com.clefal.lootbeams.config.ConfigHandlers;
import com.clefal.lootbeams.modules.ModulesManager;
import com.clefal.lootbeams.modules.tooltip.overlay.AdvanceTooltipOverlay;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
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
        System.out.println("detect register!");
        event.registerAbove(VanillaGuiOverlay.CHAT_PANEL.id(), LootBeams.MODID + "test", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {

            new AdvanceTooltipOverlay().render(guiGraphics, partialTick, screenWidth, screenHeight);
        });
    }


    @SubscribeEvent
    public static void registerModules(FMLClientSetupEvent event) {
        System.out.println("register all modules");
        ModulesManager.registerAll();
        ConfigHandlers.init();
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        registerShader(event, "particle_add", DefaultVertexFormat.PARTICLE, (s) -> {
            PARTICLE_ADDITIVE_MULTIPLY = s;
        });
    }

    private static void registerShader(RegisterShadersEvent event, String id, VertexFormat format, Consumer<ShaderInstance> callback) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("lootbeams", id), format), callback);
    }
}
