package com.lootbeams.mixin.client;

import com.lootbeams.LootBeams;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.modules.beam.BeamRenderer;
import com.lootbeams.data.LBItemEntity;
import com.lootbeams.data.LBItemEntityCache;
import com.lootbeams.utils.Checker;
import com.mojang.blaze3d.vertex.PoseStack;
import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {


    @Inject(
            method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            shift = At.Shift.AFTER
    )
    )
    private void lootBeamHook(Entity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        if (!(entity instanceof ItemEntity itemEntity)) return;
        Lazy<SoftReference<LBItemEntity>> lbItemEntity = Lazy.of(() -> LBItemEntityCache.ask(itemEntity));
        if (lbItemEntity.get() == null) return;
        LBItemEntity lbItemEntity1 = lbItemEntity.get().get();
        boolean shouldRender = ConfigurationManager.<Boolean>request(Config.ALL_ITEMS)
                || (ConfigurationManager.<Boolean>request(Config.ONLY_EQUIPMENT) && Checker.isEquipmentItem(itemEntity.getItem().getItem()))
                || (ConfigurationManager.<Boolean>request(Config.ONLY_RARE) && lbItemEntity1.isCommon())
                || (Checker.isItemInRegistryList(ConfigurationManager.request(Config.WHITELIST), itemEntity.getItem().getItem())
                && !Checker.isItemInRegistryList(ConfigurationManager.request(Config.BLACKLIST), itemEntity.getItem().getItem()));

        if (!(shouldRender && (!(ConfigurationManager.<Boolean>request(Config.REQUIRE_ON_GROUND)) || itemEntity.onGround())))
            return;

        if (ConfigurationManager.request(Config.ENABLE_BEAM)) {

            EntityRenderDispatcherHookEvent.RenderLootBeamEvent renderLootBeamEvent = new EntityRenderDispatcherHookEvent.RenderLootBeamEvent(lbItemEntity1, worldX, worldY, worldZ, entityYRot, partialTicks, poseStack, buffers, light);
            LootBeams.EVENT_BUS.post(renderLootBeamEvent);
        }

        Config.TooltipsStatus request = ConfigurationManager.request(Config.ENABLE_TOOLTIPS);
        if (request != Config.TooltipsStatus.NONE) {
            EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent renderLBTooltipsEvent = new EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent(lbItemEntity1, worldX, worldY, worldZ, entityYRot, partialTicks, poseStack, buffers, light);
            LootBeams.EVENT_BUS.post(renderLBTooltipsEvent);
        }


    }


}
