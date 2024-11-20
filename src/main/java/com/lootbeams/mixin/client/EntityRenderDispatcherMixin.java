package com.lootbeams.mixin.client;

import com.lootbeams.Configuration;
import com.lootbeams.modules.beam.BeamRenderer;
import com.lootbeams.utils.Checker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        if (shouldRender && (!Configuration.REQUIRE_ON_GROUND.get() || itemEntity.onGround())) {

            poseStack.pushPose();
            poseStack.translate(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());

            BeamRenderer.renderLootBeam(poseStack, buffers, partialTicks, itemEntity.level().getGameTime(), itemEntity, cameraOrientation());
            poseStack.popPose();
        }
    }


}
