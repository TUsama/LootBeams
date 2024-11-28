package com.lootbeams.modules.tooltip.nametag;

import com.lootbeams.Configuration;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.data.LBItemEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NameTagRenderer {
    public static final Map<ItemEntity, List<Component>> TOOLTIP_CACHE = new ConcurrentHashMap<>();

    public static void renderNameTag(PoseStack stack, MultiBufferSource buffer, LBItemEntity LBItemEntity) {
        ItemEntity item = LBItemEntity.item();

        DynamicHolder<LootRarity> rarity = GemInstance.unsocketed(item.getItem()).rarity();
        //System.out.println(rarity.isBound());
        if (Minecraft.getInstance().player.isCrouching() || ((((Boolean) ConfigurationManager.request(Config.RENDER_NAMETAGS_ONLOOK)) && isLookingAt(Minecraft.getInstance().player, item, Configuration.NAMETAG_LOOK_SENSITIVITY.get())))) {

            Color color = LBItemEntity.rarity().color();
            int rgb = color.getRGB();
/*
            {
                stack.pushPose();
                stack.mulPose(camera);
                {
                    //Render nametags at heights based on player distance
                    VertexConsumer buffer1 = buffer.getBuffer(TooltipRenderType.TOOLTIPS_BACKGROUND);
                    float nametagScale = ((Double) ConfigurationManager.request(Config.NAMETAG_SCALE)).floatValue();
                    stack.scale(-0.02F * nametagScale, -0.02F * nametagScale, 0.02F * nametagScale);

                    //Render stack counts on nametag
                    Font fontrenderer = Minecraft.getInstance().font;
                    String itemName = StringUtil.stripColor(item.getItem().getHoverName().getString());
                    if (Configuration.RENDER_STACKCOUNT.get()) {
                        int count = item.getItem().getCount();
                        if (count > 1) {
                            itemName = itemName + " x" + count;
                        }
                    }

                }


                stack.popPose();
            }*/


            //If player is crouching or looking at the item
            if (Minecraft.getInstance().player.isCrouching() || (Configuration.RENDER_NAMETAGS_ONLOOK.get() && isLookingAt(Minecraft.getInstance().player, item, ConfigurationManager.request(Config.NAMETAG_LOOK_SENSITIVITY)))) {
                float foregroundAlpha = ConfigurationManager.request(Double.class, Config.NAMETAG_TEXT_ALPHA).floatValue();
                float backgroundAlpha = ConfigurationManager.request(Double.class, Config.NAMETAG_BACKGROUND_ALPHA).floatValue();
                double yOffset = ConfigurationManager.request(Double.class, Config.NAMETAG_Y_OFFSET);
                int foregroundColor = rgb;
                int backgroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * backgroundAlpha)).getRGB();
                stack.pushPose();

                //Render nametags at heights based on player distance
                stack.translate(0.0D, Math.min(1D, Minecraft.getInstance().player.distanceToSqr(item) * 0.025D) + yOffset, 0.0D);
                stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

                float nametagScale = Configuration.NAMETAG_SCALE.get().floatValue();
                stack.scale(-0.02F * nametagScale, -0.02F * nametagScale, 0.02F * nametagScale);

                //Render stack counts on nametag
                Font fontrenderer = Minecraft.getInstance().font;

                List<Component> ask = NameTagCache.ask(LBItemEntity);
                if (ask == null) return;


                stack.translate(0, 2, -10);

                for (Component c : ask) {
                    String s = c.getString();
                    if (s.isBlank()) continue;
                    renderText(fontrenderer, stack, buffer, s, foregroundColor, backgroundColor, backgroundAlpha);
                    stack.translate(0, Minecraft.getInstance().font.lineHeight, 0.0f);

                }


                stack.popPose();
                //Move closer to the player so we dont render in beam, and render the tag


/*
                //Render small tags
                stack.translate(0.0D, 10, 0.0D);
                stack.scale(0.75f, 0.75f, 0.75f);
                boolean textDrawn = false;
                List<Component> tooltip;

                Either<Boolean, List<Component>> ask1 = NameTagCache.ask(item);
                if (ask1.right().isEmpty()) return;
                List<Component> right = ask1.right().get();*/


            }
        }

    }

    private static void renderText(Font fontRenderer, PoseStack stack, MultiBufferSource buffer, String text, int foregroundColor, int backgroundColor, float backgroundAlpha) {

        if (Configuration.BORDERS.get()) {
            float w = -fontRenderer.width(text) / 2f;
            int bg = new Color(0, 0, 0, (int) (255 * backgroundAlpha)).getRGB();
            Component comp = Component.literal(text);
            fontRenderer.drawInBatch8xOutline(comp.getVisualOrderText(), w, 0f, foregroundColor, bg, stack.last().pose(), buffer, LightTexture.FULL_BRIGHT);
        } else {
            fontRenderer.drawInBatch(text, (float) (-fontRenderer.width(text) / 2), 0f, foregroundColor, false, stack.last().pose(), buffer, Font.DisplayMode.NORMAL, backgroundColor, 15728864);
        }
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
}
