package com.clefal.lootbeams.modules;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.config.Config;
import com.clefal.lootbeams.config.ConfigurationManager;
import com.clefal.lootbeams.config.impl.TooltipsEnableStatus;
import com.clefal.lootbeams.data.LBItemEntity;
import com.clefal.lootbeams.data.LBItemEntityCache;
import com.clefal.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.clefal.lootbeams.utils.Checker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Hooker {

    public static void lootBeamEntityDispatcherHook(Entity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        if (!(entity instanceof ItemEntity itemEntity)) return;

        LBItemEntity lbItemEntity1 = LBItemEntityCache.ask(itemEntity);
        boolean shouldRender = ConfigurationManager.<Boolean>request(Config.ALL_ITEMS)
                || (ConfigurationManager.<Boolean>request(Config.ONLY_EQUIPMENT) && Checker.isEquipmentItem(itemEntity.getItem().getItem()))
                || (ConfigurationManager.<Boolean>request(Config.ONLY_RARE) && lbItemEntity1.isRare())
                || (Checker.isItemInRegistryList(ConfigurationManager.request(Config.WHITELIST), itemEntity.getItem().getItem())
                && !Checker.isItemInRegistryList(ConfigurationManager.request(Config.BLACKLIST), itemEntity.getItem().getItem()));

        if (!(shouldRender && (!(ConfigurationManager.<Boolean>request(Config.REQUIRE_ON_GROUND)) || itemEntity.onGround())))
            return;
        if (ConfigurationManager.request(Config.ENABLE_BEAM)) {

            EntityRenderDispatcherHookEvent.RenderLootBeamEvent renderLootBeamEvent = new EntityRenderDispatcherHookEvent.RenderLootBeamEvent(lbItemEntity1, worldX, worldY, worldZ, entityYRot, partialTicks, poseStack, buffers, light);
            LootBeams.EVENT_BUS.post(renderLootBeamEvent);
        }

        TooltipsEnableStatus.TooltipsStatus request = ConfigurationManager.request(Config.ENABLE_TOOLTIPS);
        if (request != TooltipsEnableStatus.TooltipsStatus.NONE) {
            EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent renderLBTooltipsEvent = new EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent(lbItemEntity1, worldX, worldY, worldZ, entityYRot, partialTicks, poseStack, buffers, light);
            LootBeams.EVENT_BUS.post(renderLBTooltipsEvent);
        }


    }
}
