package com.lootbeams.modules;

import com.lootbeams.LootBeams;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.data.LBItemEntity;
import com.lootbeams.data.LBItemEntityCache;
import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.utils.Checker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Hooker {

    public static void lootBeamHook(Entity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
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

        Config.TooltipsStatus request = ConfigurationManager.request(Config.ENABLE_TOOLTIPS);
        if (request != Config.TooltipsStatus.NONE) {
            EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent renderLBTooltipsEvent = new EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent(lbItemEntity1, worldX, worldY, worldZ, entityYRot, partialTicks, poseStack, buffers, light);
            LootBeams.EVENT_BUS.post(renderLBTooltipsEvent);
        }


    }
}
