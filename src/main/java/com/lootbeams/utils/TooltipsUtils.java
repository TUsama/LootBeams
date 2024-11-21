package com.lootbeams.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2ic;

import java.util.List;

@UtilityClass
public class TooltipsUtils {

    public void renderTooltip(Font pFont, PoseStack poseStack, List<ClientTooltipComponent> pComponents, int pMouseX, int pMouseY, ClientTooltipPositioner pTooltipPositioner) {
        if (!pComponents.isEmpty()) {
            net.minecraftforge.client.event.RenderTooltipEvent.Pre preEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipPre(ItemStack.EMPTY, this, pMouseX, pMouseY, guiWidth(), guiHeight(), pComponents, pFont, pTooltipPositioner);
            if (preEvent.isCanceled()) return;
            int i = 0;
            int j = pComponents.size() == 1 ? -2 : 0;

            for(ClientTooltipComponent clienttooltipcomponent : pComponents) {
                int k = clienttooltipcomponent.getWidth(preEvent.getFont());
                if (k > i) {
                    i = k;
                }

                j += clienttooltipcomponent.getHeight();
            }

            int i2 = i;
            int j2 = j;
            Vector2ic vector2ic = pTooltipPositioner.positionTooltip(this.guiWidth(), this.guiHeight(), preEvent.getX(), preEvent.getY(), i2, j2);
            int l = vector2ic.x();
            int i1 = vector2ic.y();
            this.pose.pushPose();
            int j1 = 400;
            this.drawManaged(() -> {
                net.minecraftforge.client.event.RenderTooltipEvent.Color colorEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipColor(this.tooltipStack, this, l, i1, preEvent.getFont(), pComponents);
                TooltipRenderUtil.renderTooltipBackground(this, l, i1, i2, j2, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), colorEvent.getBorderStart(), colorEvent.getBorderEnd());
            });
            this.pose.translate(0.0F, 0.0F, 400.0F);
            int k1 = i1;

            for(int l1 = 0; l1 < pComponents.size(); ++l1) {
                ClientTooltipComponent clienttooltipcomponent1 = pComponents.get(l1);
                clienttooltipcomponent1.renderText(preEvent.getFont(), l, k1, this.pose.last().pose(), this.bufferSource);
                k1 += clienttooltipcomponent1.getHeight() + (l1 == 0 ? 2 : 0);
            }

            k1 = i1;

            for(int k2 = 0; k2 < pComponents.size(); ++k2) {
                ClientTooltipComponent clienttooltipcomponent2 = pComponents.get(k2);
                clienttooltipcomponent2.renderImage(preEvent.getFont(), l, k1, this);
                k1 += clienttooltipcomponent2.getHeight() + (k2 == 0 ? 2 : 0);
            }

            this.pose.popPose();
        }
    }
}
