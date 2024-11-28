package com.lootbeams.modules.beam;

import com.lootbeams.Configuration;
import com.lootbeams.LootBeamsClient;
import com.lootbeams.modules.compat.apothesis.ApotheosisCompat;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.data.LBItemEntity;
import com.lootbeams.modules.beam.vfx.VFXParticle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeamRenderer {
    public static final List<ItemEntity> LIGHT_CACHE = new ArrayList<>();
    private static final Random RANDOM = new Random();


    public static void renderLootBeam(PoseStack stack, MultiBufferSource buffer, float pticks, LBItemEntity LBItemEntity, Quaternionf quaternionf) {
        ItemEntity itemEntity = LBItemEntity.item();


        Color color = LBItemEntity.rarity().color();
        float R = color.getRed() / 255f;
        float G = color.getGreen() / 255f;
        float B = color.getBlue() / 255f;

        Double preBeamAlpha = ConfigurationManager.request(Config.BEAM_ALPHA);

        float entityTime = itemEntity.tickCount;
        double distance = Minecraft.getInstance().player.distanceTo(itemEntity);
        float fadeDistance = ((Double) ConfigurationManager.request(Config.BEAM_FADE_DISTANCE)).floatValue();
        //Clefal: we don't actually need that much beamAlpha gimmick.
        //We should never cancel the beam, just make it hard to see.
        if (distance > fadeDistance) {
            float m = (float) distance - fadeDistance;

            preBeamAlpha *= 1 / Math.max(m / fadeDistance, 1.0f);
        }


        float beamRadius = 0.05f * ((Double) ConfigurationManager.request(Config.BEAM_RADIUS)).floatValue();
        float beamHeight = ((Double) ConfigurationManager.request(Config.BEAM_HEIGHT)).floatValue();
        float yOffset = ((Double) ConfigurationManager.request(Config.BEAM_Y_OFFSET)).floatValue();
        if (ConfigurationManager.request(Config.COMMON_SHORTER_BEAM)) {
            if (!compatRarityCheck(itemEntity, false)) {
                beamHeight *= 0.65f;
                yOffset -= yOffset;
            }
        }


        //I will rewrite the beam rendering code soon! I promise!
        var beamAlpha = preBeamAlpha.floatValue();


        stack.pushPose();
        stack.mulPose(quaternionf);
        //Render main beam
        {
            stack.pushPose();
            stack.translate(0, yOffset, 0);
            stack.translate(0, 1, 0);
            VertexConsumer buffer1 = buffer.getBuffer(BeamRenderType.LOOT_BEAM_RENDERTYPE);

            //beam
            {
                buffer1.vertex(stack.last().pose(), -beamRadius, -beamHeight, 0.01f).color(R, G, B, beamAlpha).uv(0, 0).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();

                buffer1.vertex(stack.last().pose(), -beamRadius, beamHeight, 0.01f).color(R, G, B, 0).uv(0, 1).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();

                buffer1.vertex(stack.last().pose(), beamRadius, beamHeight, 0.01f).color(R, G, B, 0).uv(1, 1).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();

                buffer1.vertex(stack.last().pose(), beamRadius, -beamHeight, 0.01f).color(R, G, B, beamAlpha).uv(1, 0).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
            }
            //shadow
            {
                float glowRadius = beamRadius * 1.35f;
                float glowAlpha = beamAlpha * 0.55f;
                buffer1.vertex(stack.last().pose(), -glowRadius, -beamHeight, 0.001f).color(R, G, B, glowAlpha).uv(0, 0).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();

                buffer1.vertex(stack.last().pose(), -glowRadius, beamHeight, 0.001f).color(R, G, B, 0).uv(0, 1).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();

                buffer1.vertex(stack.last().pose(), glowRadius, beamHeight, 0.001f).color(R, G, B, 0).uv(1, 1).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();

                buffer1.vertex(stack.last().pose(), glowRadius, -beamHeight, 0.001f).color(R, G, B, glowAlpha).uv(1, 0).uv2(15728880).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
            }


            stack.popPose();
        }

        stack.popPose();

        {
            if (Configuration.GLOW_EFFECT.get() && itemEntity.onGround()) {

                stack.pushPose();
                stack.translate(0, 0.01, 0);
                float radius = Configuration.GLOW_EFFECT_RADIUS.get().floatValue();
                if (Configuration.ANIMATE_GLOW.get()) {
                    beamAlpha *= (Math.abs(Math.cos((entityTime + pticks) / 10f)) * 0.5f + 0.5f) * 1.3f;
                    radius *= ((Math.abs(Math.cos((entityTime + pticks) / 10f) * 0.45f)) * 0.75f + 0.75f);
                }

                renderGlow(stack, buffer.getBuffer(BeamRenderType.GLOW), R, G, B, beamAlpha * 0.4f, radius);
                stack.popPose();
            }

            //TooltipRenderer.renderNameTag(stack, buffer, itemEntity, Color.BLACK);
            /*
            if (Configuration.PARTICLES.get()) {
                if (!Configuration.PARTICLE_RARE_ONLY.get()) {
                    renderParticles(pticks, itemEntity, (int) entityTime, R, G, B);
                } else {
                    boolean shouldRender1 = false;
                    shouldRender1 = compatRarityCheck(itemEntity, shouldRender1);
                    if (shouldRender1) {
                        renderParticles(pticks, itemEntity, (int) entityTime, R, G, B);
                    }
                }

            }*/
        }

        /*

        if (Configuration.RENDER_NAMETAGS.get()) {
            //TooltipRenderer.renderNameTag(stack, buffer, item, color);
        }

*/

    }

    public static boolean compatRarityCheck(ItemEntity item, boolean shouldRender) {
        if (ModList.get().isLoaded("apotheosis")) {
            if (ApotheosisCompat.isApotheosisItem(item.getItem())) {
                shouldRender = !ApotheosisCompat.getRarityName(item.getItem()).contains("apotheosis:common") || item.getItem().getRarity() != Rarity.COMMON;
            } else if (item.getItem().getRarity() != Rarity.COMMON) {
                shouldRender = true;
            }
        } else {
            if (item.getItem().getRarity() != Rarity.COMMON) {
                shouldRender = true;
            }
        }
        return shouldRender;
    }

    private static void renderParticles(float pticks, ItemEntity item, int entityTime, float r, float g, float b) {
        float particleCount = Math.abs(20 - Configuration.PARTICLE_COUNT.get().floatValue());
        if (entityTime % particleCount == 0 && pticks < 0.3f && !Minecraft.getInstance().isPaused()) {
            Vec3 randomDir = new Vec3(RANDOM.nextDouble(-Configuration.PARTICLE_SPEED.get() / 2.0f, Configuration.PARTICLE_SPEED.get() / 5.0f),
                    RANDOM.nextDouble(Configuration.PARTICLE_SPEED.get() / 2f, Configuration.PARTICLE_SPEED.get()),
                    RANDOM.nextDouble(-Configuration.PARTICLE_SPEED.get() / 2.0f, Configuration.PARTICLE_SPEED.get() / 2.0f))
                    .multiply(
                            Configuration.RANDOMNESS_INTENSITY.get(),
                            Configuration.RANDOMNESS_INTENSITY.get(),
                            Configuration.RANDOMNESS_INTENSITY.get()
                    );
            Vec3 particleDir = new Vec3(
                    Configuration.PARTICLE_DIRECTION_X.get(),
                    Configuration.PARTICLE_DIRECTION_Y.get(),
                    Configuration.PARTICLE_DIRECTION_Z.get()
            ).multiply(randomDir);
            addParticle(LootBeamsClient.GLOW_TEXTURE, r, g, b, 1.0f, Configuration.PARTICLE_LIFETIME.get(), RANDOM.nextFloat((float) (0.25f * Configuration.PARTICLE_SIZE.get()), (float) (1.1f * Configuration.PARTICLE_SIZE.get())), new Vec3(
                    RANDOM.nextDouble(item.getX() - Configuration.PARTICLE_RADIUS.get(), item.getX() + Configuration.PARTICLE_RADIUS.get()),
                    RANDOM.nextDouble(item.getY() - (Configuration.PARTICLE_RADIUS.get() / 3f), item.getY() + (Configuration.PARTICLE_RADIUS.get() / 3f)),
                    RANDOM.nextDouble(item.getZ() - Configuration.PARTICLE_RADIUS.get(), item.getZ() + Configuration.PARTICLE_RADIUS.get())), particleDir, item.position());
        }
    }


    private static void addParticle(ResourceLocation spriteLocation, float red, float green, float blue, float alpha, int lifetime, float size, Vec3 pos, Vec3 motion, Vec3 sourcePos) {
        Minecraft mc = Minecraft.getInstance();
        //make the particle brighter
        alpha *= 1.5f;
        VFXParticle provider = new VFXParticle(mc.level, mc.particleEngine.textureAtlas.getSprite(spriteLocation), red, green, blue, alpha, lifetime, size, pos, motion, 0, false, true);
        provider.setParticleCenter(sourcePos);
        mc.particleEngine.add(provider);
    }

    private static void renderGlow(PoseStack stack, VertexConsumer builder, float red, float green, float blue, float alpha, float radius) {
        PoseStack.Pose matrixentry = stack.last();
        Matrix4f matrixpose = matrixentry.pose();
        Matrix3f matrixnormal = matrixentry.normal();
        // draw a quad on the xz plane facing up with a radius of 0.5
        builder.vertex(matrixpose, -radius, (float) 0, -radius).color(red, green, blue, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrixnormal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrixpose, -radius, (float) 0, radius).color(red, green, blue, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrixnormal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrixpose, radius, (float) 0, radius).color(red, green, blue, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrixnormal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrixpose, radius, (float) 0, -radius).color(red, green, blue, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrixnormal, 0.0F, 1.0F, 0.0F).endVertex();
    }


}
