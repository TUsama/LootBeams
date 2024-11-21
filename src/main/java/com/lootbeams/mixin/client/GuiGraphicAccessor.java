package com.lootbeams.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = GuiGraphics.class)
public interface GuiGraphicAccessor {
    @Invoker("renderTooltip")
    private static void renderTooltip(Font pFont, ItemStack pStack, int pMouseX, int pMouseY) {
        throw new IllegalArgumentException();
    }

}
