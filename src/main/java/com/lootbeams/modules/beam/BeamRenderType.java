package com.lootbeams.modules.beam;

import com.lootbeams.Configuration;
import com.lootbeams.LootBeams;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class BeamRenderType {
    /**
     * ISSUES:
     * Beam renders behind things like chests/clouds/water/beds/entities.
     */

    public static final ResourceLocation LOOT_BEAM_TEXTURE = new ResourceLocation(LootBeams.MODID, "textures/entity/loot_beam.png");
    public static final ResourceLocation WHITE_TEXTURE = new ResourceLocation(LootBeams.MODID, "textures/entity/white.png");
    public static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(LootBeams.MODID, "textures/entity/glow.png");
    protected static final RenderType GLOW = Configuration.GLOWING_BEAM.get() ? RenderType.entityTranslucentEmissive(GLOW_TEXTURE) : RenderType.entityCutout(GLOW_TEXTURE);
    public static final RenderType LOOT_BEAM_RENDERTYPE = Configuration.GLOWING_BEAM.get() ? RenderType.lightning() : createRenderType();


    public static RenderType createRenderType() {
        ResourceLocation texture = !Configuration.SOLID_BEAM.get() ? LOOT_BEAM_TEXTURE : WHITE_TEXTURE;
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_BEACON_BEAM_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(false);

        return RenderType.create("loot_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true, state);
    }
}
