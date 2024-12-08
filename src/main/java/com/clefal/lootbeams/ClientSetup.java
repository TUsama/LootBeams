package com.clefal.lootbeams;

import com.clefal.lootbeams.modules.beam.BeamRenderer;
import com.clefal.lootbeams.modules.tooltip.nametag.NameTagRenderer;
import com.clefal.lootbeams.utils.Checker;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = LootBeams.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientSetup {

	public static void init(FMLClientSetupEvent ignored) {
		ignored.enqueueWork(() -> {
			//MinecraftForge.EVENT_BUS.addListener(ClientSetup::onRenderNameplate);
			MinecraftForge.EVENT_BUS.addListener(ClientSetup::onItemCreation);
			MinecraftForge.EVENT_BUS.addListener(ClientSetup::entityRemoval);
			MinecraftForge.EVENT_BUS.addListener(ClientSetup::onLevelRender);
		});
	}


	@SubscribeEvent
	public static void onHudRender(RenderGuiOverlayEvent.Post event) {

/*
		if(event.getOverlay().equals(VanillaGuiOverlay.CROSSHAIR.type())){
			if(Configuration.ADVANCED_TOOLTIPS.get() && (Minecraft.getInstance().screen == null || Minecraft.getInstance().screen instanceof ChatScreen)) {
				Player player = Minecraft.getInstance().player;
				HitResult result = getEntityItem(player);
				if(result != null && result.getType() == HitResult.Type.ENTITY) {
					if(((EntityHitResult)result).getEntity() instanceof ItemEntity itemEntity) {
						if(Configuration.REQUIRE_ON_GROUND.get() && !itemEntity.onGround()) return;
						int x = event.getWindow().getGuiScaledWidth() / 2;
						int rarityX = x;
						int y = event.getWindow().getGuiScaledHeight() / 2;
						List<Component> tooltipLines = Screen.getTooltipFromItem(Minecraft.getInstance(), itemEntity.getItem());
						if(Configuration.WORLDSPACE_TOOLTIPS.get()){
							Vec3 tooltipWorldPos = itemEntity.position().add(
									0,
									Math.min(1D, Minecraft.getInstance().player.distanceToSqr(itemEntity) * 0.025D)
											+ Configuration.NAMETAG_Y_OFFSET.get() +
											(Screen.getTooltipFromItem(Minecraft.getInstance(), itemEntity.getItem()).size())/100f,
									0);
							Vector3f desiredScreenSpacePos = worldToScreenSpace(tooltipWorldPos, event.getPartialTick());

							desiredScreenSpacePos = new Vector3f(Mth.clamp(
									desiredScreenSpacePos.x(),
									0,
									event.getWindow().getGuiScaledWidth()),
									Mth.clamp(
											desiredScreenSpacePos.y(),
											0,
											event.getWindow().getGuiScaledHeight() - (Minecraft.getInstance().font.lineHeight * Screen.getTooltipFromItem(Minecraft.getInstance(), itemEntity.getItem()).size())),
									desiredScreenSpacePos.z());
							Component longestLine =
									tooltipLines.stream().max((a, b) -> Minecraft.getInstance().font.width(a) - Minecraft.getInstance().font.width(b))
											.orElse(Screen.getTooltipFromItem(Minecraft.getInstance(), itemEntity.getItem()).get(0));
							if(Configuration.SCREEN_TOOLTIPS_REQUIRE_CROUCH.get() && !player.isCrouching()) longestLine = tooltipLines.get(0);
							x = (int)desiredScreenSpacePos.x() - 10 - Minecraft.getInstance().font.width(longestLine) / 2;
							rarityX = (int)desiredScreenSpacePos.x() - 12 - Minecraft.getInstance().font.width(Provider.getRarity(itemEntity.getItem())) / 2;
							y = (int)desiredScreenSpacePos.y();
						}
						int guiScale = Minecraft.getInstance().options.guiScale().get();
						if(tooltipLines.size() > 6) {
							Minecraft.getInstance().options.guiScale().set(1);
						}
						if((Configuration.SCREEN_TOOLTIPS_REQUIRE_CROUCH.get() && player.isCrouching()) || !Configuration.SCREEN_TOOLTIPS_REQUIRE_CROUCH.get()) {
							event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, itemEntity.getItem(), x, y);
						} else {
							tooltipLines = List.of(tooltipLines.get(0), Component.literal(Provider.getRarity(itemEntity.getItem())).withStyle(itemEntity.getItem().getDisplayName().getStyle()));
							if(ModList.get().isLoaded("apotheosis")) {
								if(ApotheosisCompat.isApotheosisItem(itemEntity.getItem())) {
									tooltipLines = List.of(tooltipLines.get(0), Component.literal(Provider.getRarity(itemEntity.getItem())).withStyle(s -> s.withColor(ApotheosisCompat.getRarityColor(itemEntity.getItem()))));
								}
							}
							if(Configuration.COMBINE_NAME_AND_RARITY.get()) {
								event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, tooltipLines, itemEntity.getItem().getTooltipImage(), itemEntity.getItem(), x, y);
							} else {
								event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, List.of(tooltipLines.get(0)), itemEntity.getItem().getTooltipImage(), itemEntity.getItem(), x, y);
								event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, List.of(tooltipLines.get(1)), itemEntity.getItem().getTooltipImage(), itemEntity.getItem(), rarityX, y + Minecraft.getInstance().font.lineHeight * 2);
							}
						}
						Minecraft.getInstance().options.guiScale().set(guiScale);
					}
				}
			}
		}*/
	}




	public static void onItemCreation(EntityJoinLevelEvent event){
		if (event.getEntity() instanceof ItemEntity ie) {
			NameTagRenderer.TOOLTIP_CACHE.computeIfAbsent(ie, itemEntity -> itemEntity.getItem().getTooltipLines(null, TooltipFlag.Default.NORMAL));
			if (!BeamRenderer.LIGHT_CACHE.contains(ie)) {
				BeamRenderer.LIGHT_CACHE.add(ie);
			}
		}
	}
	public static final List<Consumer<PoseStack>> delayedRenders = new ArrayList<>();

	public static void onLevelRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			PoseStack stack = event.getPoseStack();
			stack.pushPose();
			Vec3 pos = event.getCamera().getPosition();
			stack.translate(-pos.x, -pos.y, -pos.z);
			delayedRenders.forEach(consumer -> consumer.accept(stack));
			stack.popPose();
		}
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
			delayedRenders.clear();
		}
	}

	public static void entityRemoval(EntityLeaveLevelEvent event) {
		if (event.getEntity() instanceof ItemEntity ie) {
			NameTagRenderer.TOOLTIP_CACHE.remove(ie);
			BeamRenderer.LIGHT_CACHE.remove(ie);
		}
	}

	public static int overrideLight(ItemEntity ie, int light) {
		if (Configuration.ALL_ITEMS.get()
				|| (Configuration.ONLY_EQUIPMENT.get() && Checker.isEquipmentItem(ie.getItem().getItem()))
				|| (Configuration.ONLY_RARE.get() && BeamRenderer.compatRarityCheck(ie, false))
				|| (Checker.isItemInRegistryList(Configuration.WHITELIST.get(), ie.getItem().getItem()))) {
			light = 15728640;
		}

		return light;
	}


}
