package com.lootbeams.modules.beam;

import com.google.common.collect.Lists;
import com.lootbeams.Configuration;
import com.lootbeams.LootBeams;
import com.lootbeams.ModClientEvents;
import com.lootbeams.VFXParticle;
import com.lootbeams.compat.ApotheosisCompat;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.modules.beam.color.BeamColorCache;
import com.lootbeams.modules.beam.color.BeamColorSourceContainer;
import com.lootbeams.utils.Checker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.StringDecomposer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class BeamRenderer {
    public static final List<ItemEntity> LIGHT_CACHE = new ArrayList<>();
    private static final Random RANDOM = new Random();


    public static void renderLootBeam(PoseStack stack, MultiBufferSource buffer, float pticks, long worldtime, Entity entity, Quaternionf quaternionf) {
        if (!(entity instanceof ItemEntity itemEntity)
                || Minecraft.getInstance().player.distanceToSqr(itemEntity) > Math.pow(Configuration.RENDER_DISTANCE.get(), 2)) {
            return;
        }
        Item item = itemEntity.getItem().getItem();
        boolean shouldRender = (Configuration.ALL_ITEMS.get()
                || (Configuration.ONLY_EQUIPMENT.get() && Checker.isEquipmentItem(item))
                || (Configuration.ONLY_RARE.get() && BeamRenderer.compatRarityCheck(itemEntity, false))
                || (Checker.isItemInRegistryList(Configuration.WHITELIST.get(), itemEntity.getItem().getItem())))
                && !Checker.isItemInRegistryList(Configuration.BLACKLIST.get(), itemEntity.getItem().getItem());

        if (!(shouldRender && (!Configuration.REQUIRE_ON_GROUND.get() || itemEntity.onGround()))) return;

        RenderSystem.enableDepthTest();


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
        float glowRadius = beamRadius * 1.2f;
        float beamHeight = ((Double) ConfigurationManager.request(Config.BEAM_HEIGHT)).floatValue();
        float yOffset = ((Double) ConfigurationManager.request(Config.BEAM_Y_OFFSET)).floatValue();
        if (ConfigurationManager.request(Config.COMMON_SHORTER_BEAM)) {
            if (!compatRarityCheck(itemEntity, false)) {
                beamHeight *= 0.65f;
                yOffset -= yOffset;
            }
        }
        Either<Boolean, Color> ask = BeamColorCache.ask(itemEntity);
        if (ask.right().isEmpty()) return;

        Color color = ask.right().get();
        float R = color.getRed() / 255f;
        float G = color.getGreen() / 255f;
        float B = color.getBlue() / 255f;

        //I will rewrite the beam rendering code soon! I promise!
        var beamAlpha = preBeamAlpha.floatValue();


        stack.pushPose();
        //stack.mulPose(quaternionf);
        //Render main beam
        float rotation = (float) Math.floorMod(worldtime, 40L) + pticks;
        stack.mulPose(Axis.YP.rotationDegrees(rotation * 2.25F - 45.0F));
        stack.translate(0, yOffset, 0);
        stack.translate(0, 1, 0);
        stack.mulPose(Axis.XP.rotationDegrees(180));
        VertexConsumer buffer1 = buffer.getBuffer(BeamRenderType.LOOT_BEAM_RENDERTYPE);
        //System.out.println(beamAlpha);
        renderPart(stack, buffer1, R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius, false);
        stack.mulPose(Axis.XP.rotationDegrees(-180));

        renderPart(stack, buffer1, R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius, Configuration.SOLID_BEAM.get());
        stack.popPose();
        //Stopwatch started = Stopwatch.createStarted();


        //Render glow around main beam
        stack.pushPose();
        stack.translate(0, yOffset, 0);
        stack.translate(0, 1, 0);
        stack.mulPose(Axis.XP.rotationDegrees(180));
        renderPart(stack, buffer1, R, G, B, beamAlpha * 0.4f, beamHeight, -glowRadius, -glowRadius, glowRadius, -glowRadius, -beamRadius, glowRadius, glowRadius, glowRadius, false);
        stack.mulPose(Axis.XP.rotationDegrees(-180));
        renderPart(stack, buffer1, R, G, B, beamAlpha * 0.4f, beamHeight, -glowRadius, -glowRadius, glowRadius, -glowRadius, -beamRadius, glowRadius, glowRadius, glowRadius, Configuration.SOLID_BEAM.get());
        stack.popPose();

        //System.out.println(started.stop());

        if (Configuration.WHITE_CENTER.get()) {

            stack.pushPose();
            stack.translate(0, yOffset, 0);
            stack.translate(0, 1, 0);
            stack.mulPose(Axis.XP.rotationDegrees(180));
            renderPart(stack, buffer1, R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius * 0.4f, beamRadius * 0.4f, 0.0F, -beamRadius * 0.4f, 0.0F, 0.0F, -beamRadius * 0.4f, false);
            stack.mulPose(Axis.XP.rotationDegrees(-180));
            renderPart(stack, buffer1, R, G, B, beamAlpha, beamHeight, 0.0F, beamRadius * 0.4f, beamRadius * 0.4f, 0.0F, -beamRadius * 0.4f, 0.0F, 0.0F, -beamRadius * 0.4f, Configuration.SOLID_BEAM.get());
            stack.popPose();
        }

        if (Configuration.GLOW_EFFECT.get() && itemEntity.onGround()) {

            stack.pushPose();
            stack.translate(0, 0.001f, 0);
            float radius = Configuration.GLOW_EFFECT_RADIUS.get().floatValue();
            if (Configuration.ANIMATE_GLOW.get()) {
                beamAlpha *= (Math.abs(Math.cos((entityTime + pticks) / 10f)) * 0.5f + 0.5f) * 1.3f;
                radius *= ((Math.abs(Math.cos((entityTime + pticks) / 10f) * 0.45f)) * 0.75f + 0.75f);
            }

            renderGlow(stack, buffer.getBuffer(BeamRenderType.GLOW), R, G, B, beamAlpha * 0.4f, radius);
            stack.popPose();
        }
        stack.popPose();

        if (Configuration.RENDER_NAMETAGS.get()) {
            //TooltipRenderer.renderNameTag(stack, buffer, item, color);
        }

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

        }

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
            Vec3 randomDir = new Vec3(RANDOM.nextDouble(-Configuration.PARTICLE_SPEED.get() / 2.0f, Configuration.PARTICLE_SPEED.get() / 2.0f),
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
            addParticle(ModClientEvents.GLOW_TEXTURE, r, g, b, 1.0f, Configuration.PARTICLE_LIFETIME.get(), RANDOM.nextFloat((float) (0.25f * Configuration.PARTICLE_SIZE.get()), (float) (1.1f * Configuration.PARTICLE_SIZE.get())), new Vec3(
                    RANDOM.nextDouble(item.getX() - Configuration.PARTICLE_RADIUS.get(), item.getX() + Configuration.PARTICLE_RADIUS.get()),
                    RANDOM.nextDouble(item.getY() - (Configuration.PARTICLE_RADIUS.get() / 3f), item.getY() + (Configuration.PARTICLE_RADIUS.get() / 3f)),
                    RANDOM.nextDouble(item.getZ() - Configuration.PARTICLE_RADIUS.get(), item.getZ() + Configuration.PARTICLE_RADIUS.get())), particleDir, item.position());
        }
    }

    private static double pulse(float entityTime, float max) {
        double val = Math.cos(entityTime / 10f) * 0.5f + 0.5f;
        return Mth.lerp(val, 0.25f, max);
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

    /**
     * Returns the color from the item's name, rarity, tag, or override.
     */
    private static Color getItemColor(ItemEntity item) {
        if (LootBeams.CRASH_BLACKLIST.contains(item.getItem())) {
            return Color.WHITE;
        }

        try {

            //From Config Overrides
            Color override = getColorFromItemOverrides(item.getItem().getItem());
            if (override != null) {
                return override;
            }

            //From NBT
            if (item.getItem().hasTag() && item.getItem().getTag().contains("lootbeams.color")) {
                return Color.decode(item.getItem().getTag().getString("lootbeams.color"));
            }


            //From Name
            Boolean request = ConfigurationManager.request(Config.RENDER_NAME_COLOR);
            if (request) {
                Color nameColor = getRawColor(item.getItem().getHoverName());
                if (!nameColor.equals(Color.WHITE)) {
                    return nameColor;
                }
            }

            //From Rarity
            if (Configuration.RENDER_RARITY_COLOR.get() && item.getItem().getRarity().getStyleModifier().apply(Style.EMPTY).getColor() != null) {
                return new Color(item.getItem().getRarity().getStyleModifier().apply(Style.EMPTY).getColor().getValue());
            } else {
                return Color.WHITE;
            }
        } catch (Exception e) {
            LootBeams.LOGGER.error("Failed to get color for (" + item.getItem().getDisplayName() + "), added to temporary blacklist");
            LootBeams.CRASH_BLACKLIST.add(item.getItem());
            LootBeams.LOGGER.info("Temporary blacklist is now : ");
            for (ItemStack s : LootBeams.CRASH_BLACKLIST) {
                LootBeams.LOGGER.info(s.getDisplayName());
            }
            return Color.WHITE;
        }
    }

    /**
     * Gets color from the first letter in the text component.
     */
    public static Color getRawColor(Component text) {
        List<Style> list = Lists.newArrayList();
        text.visit((acceptor, styleIn) -> {
            StringDecomposer.iterateFormatted(styleIn, acceptor, (string, style, consumer) -> {
                list.add(style);
                return true;
            });
            return Optional.empty();
        }, Style.EMPTY);
        if (list.get(0).getColor() != null) {
            return new Color(list.get(0).getColor().getValue());
        }
        return Color.WHITE;
    }

    private static void renderPart(PoseStack stack, VertexConsumer builder, float red, float green, float blue, float alpha, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8, boolean gradient) {
        if (gradient) {
            renderGradientPart(stack, builder, red, green, blue, alpha, height, radius_1, radius_2, radius_3, radius_4, radius_5, radius_6, radius_7, radius_8);
        } else {

            renderPart(stack, builder, red, green, blue, alpha, height, radius_1, radius_2, radius_3, radius_4, radius_5, radius_6, radius_7, radius_8);
        }
    }

    public static String getRarity(ItemStack stack) {
        String rarity = stack.getRarity().name().toLowerCase();
        if (ModList.get().isLoaded("apotheosis")) {
            if (ApotheosisCompat.isApotheosisItem(stack)) {
                rarity = ApotheosisCompat.getRarityName(stack);
            }
        }
        rarity = rarity.replace(":", ".").replace("_", ".");
        if (I18n.exists(rarity)) {
            return I18n.get(rarity);
        }
        return rarity;
    }


    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static void renderPart(PoseStack stack, VertexConsumer builder, float red, float green, float blue, float alpha, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8) {
        PoseStack.Pose matrixentry = stack.last();
        Matrix4f matrixpose = matrixentry.pose();
        Matrix3f matrixnormal = matrixentry.normal();
        renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_1, radius_2, radius_3, radius_4);
        renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_7, radius_8, radius_5, radius_6);
        renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_3, radius_4, radius_7, radius_8);
        renderQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_5, radius_6, radius_1, radius_2);
    }

    private static void renderGradientPart(PoseStack stack, VertexConsumer builder, float red, float green, float blue, float alpha, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8) {
        PoseStack.Pose matrixentry = stack.last();
        Matrix4f matrixpose = matrixentry.pose();
        Matrix3f matrixnormal = matrixentry.normal();
        renderUpwardsGradientQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_1, radius_2, radius_3, radius_4);
        renderUpwardsGradientQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_7, radius_8, radius_5, radius_6);
        renderUpwardsGradientQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_3, radius_4, radius_7, radius_8);
        renderUpwardsGradientQuad(matrixpose, matrixnormal, builder, red, green, blue, alpha, height, radius_5, radius_6, radius_1, radius_2);
    }

    private static void renderQuad(Matrix4f pose, Matrix3f normal, VertexConsumer builder, float red, float green, float blue, float alpha, float y, float z1, float texu1, float z, float texu) {
        addVertex(pose, normal, builder, red, green, blue, alpha, y, z1, texu1, 1f, 0f);
        addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z1, texu1, 1f, 1f);
        addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z, texu, 0f, 1f);
        addVertex(pose, normal, builder, red, green, blue, alpha, y, z, texu, 0f, 0f);
    }

    private static void renderUpwardsGradientQuad(Matrix4f pose, Matrix3f normal, VertexConsumer builder, float red, float green, float blue, float alpha, float y, float z1, float texu1, float z, float texu) {
        addVertex(pose, normal, builder, red, green, blue, 0.0f, y, z1, texu1, 1f, 0f);
        addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z1, texu1, 1f, 1f);
        addVertex(pose, normal, builder, red, green, blue, alpha, 0f, z, texu, 0f, 1f);
        addVertex(pose, normal, builder, red, green, blue, 0.0f, y, z, texu, 0f, 0f);
    }

    private static void addVertex(Matrix4f pose, Matrix3f normal, VertexConsumer builder, float red, float green, float blue, float alpha, float y, float x, float z, float texu, float texv) {

        builder.vertex(pose, x, y, z).color(red, green, blue, alpha).uv(texu, texv).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    /**
     * Checks if the player is looking at the given entity, accuracy determines how close the player has to look.
     */
    public static boolean isLookingAt(LocalPlayer player, Entity target, double accuracy) {
        Vec3 difference = new Vec3(target.getX() - player.getX(), target.getEyeY() - player.getEyeY(), target.getZ() - player.getZ());
        double length = difference.length();
//        double dot = player.getViewVector(1.0F).normalize().dot(difference.normalize());
        double dot = Minecraft.getInstance().getCameraEntity().getLookAngle().normalize().dot(difference.normalize());
        return dot > 1.0D - accuracy / length && !target.isInvisible();
    }

    public static Color getColorFromItemOverrides(Item i) {
        List<String> overrides = ConfigurationManager.request(Config.COLOR_OVERRIDES);
        if (overrides.size() > 0) {
            for (String unparsed : overrides.stream().filter((s) -> (!s.isEmpty())).collect(Collectors.toList())) {
                String[] configValue = unparsed.split("=");
                if (configValue.length == 2) {
                    String nameIn = configValue[0];
                    ResourceLocation registry = ResourceLocation.tryParse(nameIn.replace("#", ""));
                    Color colorIn = null;
                    try {
                        colorIn = Color.decode(configValue[1]);
                    } catch (Exception e) {
                        LootBeams.LOGGER.error(String.format("Color overrides error! \"%s\" is not a valid hex color for \"%s\"", configValue[1], nameIn));
                        return null;
                    }

                    //Modid
                    if (!nameIn.contains(":")) {
                        if (ForgeRegistries.ITEMS.getKey(i).getNamespace().equals(nameIn)) {
                            return colorIn;
                        }

                    }

                    if (registry != null) {
                        //Tag
                        if (nameIn.startsWith("#")) {
                            if (ForgeRegistries.ITEMS.tags().getTag(TagKey.create(BuiltInRegistries.ITEM.key(), registry)).contains(i)) {
                                return colorIn;
                            }
                        }

                        //Item
                        Item registryItem = ForgeRegistries.ITEMS.getValue(registry);
                        if (registryItem != null && registryItem.asItem() == i) {
                            return colorIn;
                        }

                    }
                }
            }
        }
        return null;
    }
}
