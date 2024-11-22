package com.lootbeams.mixin.client;

import com.lootbeams.Configuration;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.events.RenderLBTooltipsEvent;
import com.lootbeams.events.RenderLootBeamEvent;
import com.lootbeams.modules.beam.BeamRenderer;
import com.lootbeams.utils.Checker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    public abstract Quaternionf cameraOrientation();

    @Inject(
            method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            shift = At.Shift.AFTER
    )
    )
    private void lootBeamHook(Entity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        if (!(entity instanceof ItemEntity itemEntity)) return;
        if (Minecraft.getInstance().player.distanceToSqr(itemEntity) > Math.pow(ConfigurationManager.request(Config.RENDER_DISTANCE), 2)) return;
        boolean shouldRender = ((Boolean)ConfigurationManager.request(Config.ALL_ITEMS))
                || (((Boolean) ConfigurationManager.request(Config.ONLY_EQUIPMENT)) && Checker.isEquipmentItem(itemEntity.getItem().getItem()))
                || (((Boolean)ConfigurationManager.request(Config.ONLY_RARE)) && BeamRenderer.compatRarityCheck(itemEntity, false))
                || (Checker.isItemInRegistryList(ConfigurationManager.request(Config.WHITELIST), itemEntity.getItem().getItem())
                && !Checker.isItemInRegistryList(ConfigurationManager.request(Config.BLACKLIST), itemEntity.getItem().getItem()));

        if (!(shouldRender && (!((Boolean) ConfigurationManager.request(Config.REQUIRE_ON_GROUND)) || itemEntity.onGround()))) return;

        RenderLootBeamEvent renderLootBeamEvent = new RenderLootBeamEvent();
        RenderLBTooltipsEvent renderLBTooltipsEvent = new RenderLBTooltipsEvent();
        if (!renderLootBeamEvent.isCanceled()) {
            BeamRenderer.renderLootBeam(poseStack, buffers, partialTicks, entity.level().getGameTime(), itemEntity, cameraOrientation());
        }
        if (!renderLBTooltipsEvent.isCanceled()) {

        }
    }


}
