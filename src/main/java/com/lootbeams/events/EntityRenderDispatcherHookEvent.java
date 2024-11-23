package com.lootbeams.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class EntityRenderDispatcherHookEvent extends Event {
    public ItemEntity entity;
    public double worldX;
    public double worldY;
    public double worldZ;
    public float entityYRot;
    public float partialTicks;
    public PoseStack poseStack;
    public MultiBufferSource buffers;
    public int light;

    public EntityRenderDispatcherHookEvent(ItemEntity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
        this.entity = entity;
        this.worldX = worldX;
        this.worldY = worldY;
        this.worldZ = worldZ;
        this.entityYRot = entityYRot;
        this.partialTicks = partialTicks;
        this.poseStack = poseStack;
        this.buffers = buffers;
        this.light = light;
    }

    public static class RenderLBTooltipsEvent extends EntityRenderDispatcherHookEvent implements ICancellableEvent {
        public RenderLBTooltipsEvent(ItemEntity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
            super(entity, worldX, worldY, worldZ, entityYRot, partialTicks, poseStack, buffers, light);
        }
    }

    public static class RenderLootBeamEvent extends EntityRenderDispatcherHookEvent implements ICancellableEvent {
        public RenderLootBeamEvent(ItemEntity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
            super(entity, worldX, worldY, worldZ, entityYRot, partialTicks, poseStack, buffers, light);
        }
    }
}
