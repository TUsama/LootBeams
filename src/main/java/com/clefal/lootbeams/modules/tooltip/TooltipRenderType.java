package com.clefal.lootbeams.modules.tooltip;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.config.Config;
import com.clefal.lootbeams.config.ConfigurationManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class TooltipRenderType {
    public static final RenderType TOOLTIPS_BACKGROUND = createBackgroundRenderType();

    public static final RenderType CONNECTION = createLineRenderType();



    private static RenderType createBackgroundRenderType(){
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_GUI_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(LootBeams.MODID, "textures/tooltips/tooltips_background.png"), false, false))
                .setTransparencyState(BACKGROUND)
                .createCompositeState(false);

        return RenderType.create("loot_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true, state);
    }

    private static RenderType createLineRenderType(){
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_GUI_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(LootBeams.MODID, "textures/tooltips/connection.png"), false, false))
                .setTransparencyState(TEXT)
                .createCompositeState(false);

        return RenderType.create("loot_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true, state);
    }

    private static final RenderStateShard.TransparencyStateShard TEXT = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, ((Double) ConfigurationManager.request(Config.NAMETAG_TEXT_ALPHA)).floatValue());
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    private static final RenderStateShard.TransparencyStateShard BACKGROUND = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, ((Double) ConfigurationManager.request(Config.NAMETAG_BACKGROUND_ALPHA)).floatValue());
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
}
