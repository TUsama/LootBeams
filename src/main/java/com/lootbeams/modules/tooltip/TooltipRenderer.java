package com.lootbeams.modules.tooltip;

import com.lootbeams.Configuration;
import com.lootbeams.modules.beam.BeamRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fml.ModList;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TooltipRenderer {
    public static final Map<ItemEntity, List<Component>> TOOLTIP_CACHE = new ConcurrentHashMap<>();

    public static void renderNameTag(PoseStack stack, MultiBufferSource buffer, ItemEntity item, Color color) {
        if (Configuration.ADVANCED_TOOLTIPS.get()) return;
        //If player is crouching or looking at the item
        if (Minecraft.getInstance().player.isCrouching() || (Configuration.RENDER_NAMETAGS_ONLOOK.get() && BeamRenderer.isLookingAt(Minecraft.getInstance().player, item, Configuration.NAMETAG_LOOK_SENSITIVITY.get()))) {
            float foregroundAlpha = Configuration.NAMETAG_TEXT_ALPHA.get().floatValue();
            float backgroundAlpha = Configuration.NAMETAG_BACKGROUND_ALPHA.get().floatValue();
            double yOffset = Configuration.NAMETAG_Y_OFFSET.get();
            int foregroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * foregroundAlpha)).getRGB();
            int backgroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * backgroundAlpha)).getRGB();

            stack.pushPose();

            //Render nametags at heights based on player distance
            stack.translate(0.0D, Math.min(1D, Minecraft.getInstance().player.distanceToSqr(item) * 0.025D) + yOffset, 0.0D);
            stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

            float nametagScale = Configuration.NAMETAG_SCALE.get().floatValue();
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

            //Move closer to the player so we dont render in beam, and render the tag
            stack.translate(0, 0, -10);
            renderText(fontrenderer, stack, buffer, itemName, foregroundColor, backgroundColor, backgroundAlpha);

            //Render small tags
            stack.translate(0.0D, 10, 0.0D);
            stack.scale(0.75f, 0.75f, 0.75f);
            boolean textDrawn = false;
            List<net.minecraft.network.chat.Component> tooltip;
            if (!TOOLTIP_CACHE.containsKey(item)) {
                tooltip = item.getItem().getTooltipLines(null, TooltipFlag.Default.NORMAL);
                TOOLTIP_CACHE.put(item, tooltip);
            } else {
                tooltip = TOOLTIP_CACHE.get(item);
            }
            if (tooltip.size() >= 2) {
                net.minecraft.network.chat.Component tooltipRarity = tooltip.get(1);

                //Render dmcloot rarity small tags
                if (Configuration.DMCLOOT_COMPAT_RARITY.get() && ModList.get().isLoaded("dmcloot")) {
                    if (item.getItem().hasTag() && item.getItem().getTag().contains("dmcloot.rarity")) {
                        Color rarityColor = Configuration.WHITE_RARITIES.get() ? Color.WHITE : BeamRenderer.getRawColor(tooltipRarity);
                        net.minecraft.network.chat.Component translatedRarity = Component.translatable("rarity.dmcloot." + item.getItem().getTag().getString("dmcloot.rarity"));
                        renderText(fontrenderer, stack, buffer, translatedRarity.getString(), rarityColor.getRGB(), backgroundColor, backgroundAlpha);
                        textDrawn = true;
                    }
                }

                //Render custom rarities
                if (!textDrawn && Configuration.CUSTOM_RARITIES.get().contains(tooltipRarity.getString())) {
                    Color rarityColor = Configuration.WHITE_RARITIES.get() ? Color.WHITE : BeamRenderer.getRawColor(tooltipRarity);
                    foregroundColor = new Color(rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), (int) (255 * foregroundAlpha)).getRGB();
                    backgroundColor = new Color(rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), (int) (255 * backgroundAlpha)).getRGB();
                    renderText(fontrenderer, stack, buffer, tooltipRarity.getString(), foregroundColor, backgroundColor, backgroundAlpha);
                }

            }
            if (!textDrawn && Configuration.VANILLA_RARITIES.get()) {
                Color rarityColor = BeamRenderer.getRawColor(tooltip.get(0));
                foregroundColor = new Color(rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), (int) (255 * foregroundAlpha)).getRGB();
                backgroundColor = new Color(rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), (int) (255 * backgroundAlpha)).getRGB();
                renderText(fontrenderer, stack, buffer, BeamRenderer.getRarity(item.getItem()), foregroundColor, backgroundColor, backgroundAlpha);
            }

            stack.popPose();
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
}
