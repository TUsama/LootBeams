package com.lootbeams;

import com.lootbeams.modules.beam.BeamRenderer;
import com.lootbeams.modules.tooltip.nametag.NameTagRenderer;
import com.lootbeams.utils.Checker;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase.equals(TickEvent.Phase.START)) {
			if(playSoundCooldown > 0) playSoundCooldown--;
		}
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
							desiredScreenSpacePos = new Vector3f(Mth.clamp(desiredScreenSpacePos.x(), 0, event.getWindow().getGuiScaledWidth()), Mth.clamp(desiredScreenSpacePos.y(), 0, event.getWindow().getGuiScaledHeight() - (Minecraft.getInstance().font.lineHeight * Screen.getTooltipFromItem(Minecraft.getInstance(), itemEntity.getItem()).size())), desiredScreenSpacePos.z());
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

	public static Vector3f worldToScreenSpace(Vec3 pos, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Camera camera = mc.gameRenderer.getMainCamera();
		Vec3 cameraPosition = camera.getPosition();

		Vector3f position = new Vector3f((float) (cameraPosition.x - pos.x), (float) (cameraPosition.y - pos.y), (float) (cameraPosition.z - pos.z));
		Quaternionf cameraRotation = camera.rotation();
		cameraRotation.conjugate();
		//cameraRotation = restrictAxis(new Vec3(1, 1, 0), cameraRotation);
		cameraRotation.transform(position);

		// Account for view bobbing
		if (mc.options.bobView.get() && mc.getCameraEntity() instanceof Player) {
			Player player = (Player) mc.getCameraEntity();
			float playerStep = player.walkDist - player.walkDistO;
			float stepSize = -(player.walkDist + playerStep * partialTicks);
			float viewBob = Mth.lerp(partialTicks, player.oBob, player.bob);

			Quaternionf bobXRotation = Axis.XP.rotationDegrees(Math.abs(Mth.cos(stepSize * (float) Math.PI - 0.2f) * viewBob) * 5f);
			Quaternionf bobZRotation = Axis.ZP.rotationDegrees(Mth.sin(stepSize * (float) Math.PI) * viewBob * 3f);
			bobXRotation.conjugate();
			bobZRotation.conjugate();
			bobXRotation.transform(position);
			bobZRotation.transform(position);
			position.add(Mth.sin(stepSize * (float) Math.PI) * viewBob * 0.5f, Math.abs(Mth.cos(stepSize * (float) Math.PI) * viewBob), 0f);
		}

		Window window = mc.getWindow();
		float screenSize = window.getGuiScaledHeight() / 2f / position.z() / (float) Math.tan(Math.toRadians(mc.gameRenderer.getFov(camera, partialTicks, true) / 2f));
		position.mul(-screenSize, -screenSize, 1f);
		position.add(window.getGuiScaledWidth() / 2f, window.getGuiScaledHeight() / 2f, 0f);

		return position;
	}

	public static HitResult getEntityItem(Player player) {
		Minecraft mc = Minecraft.getInstance();
		double distance = player.getBlockReach();
		float partialTicks = mc.getDeltaFrameTime();
		Vec3 position = player.getEyePosition(partialTicks);
		Vec3 view = player.getViewVector(partialTicks);
		if (mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS)
			distance = mc.hitResult.getLocation().distanceTo(position);
		return getEntityItem(player, position, position.add(view.x * distance, view.y * distance, view.z * distance));

	}
	public static HitResult getEntityItem(Player player, Vec3 position, Vec3 look) {
		Vec3 include = look.subtract(position);
		List list = player.level().getEntities(player, player.getBoundingBox().expandTowards(include.x, include.y, include.z));
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);
			if (entity instanceof ItemEntity) {
				AABB axisalignedbb = entity.getBoundingBox().inflate(0.5).inflate(0.0,0.5,0.0);
				Optional<Vec3> vec = axisalignedbb.clip(position, look);
				if (vec.isPresent())
					return new EntityHitResult(entity, vec.get());
				else if (axisalignedbb.contains(position))
					return new EntityHitResult(entity);
			}
		}
		return null;
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

	public static int playSoundCooldown = 0;
	public static void playDropSound(ItemEntity itemEntity) {
		if(playSoundCooldown != 0) return;
		if (!Configuration.SOUND.get()) {
			return;
		}

		Item item = itemEntity.getItem().getItem();
		if ((Configuration.SOUND_ALL_ITEMS.get() && !Checker.isItemInRegistryList(Configuration.BLACKLIST.get(), item))
				|| (Configuration.SOUND_ONLY_EQUIPMENT.get() && Checker.isEquipmentItem(item))
				|| (Configuration.SOUND_ONLY_RARE.get() && BeamRenderer.compatRarityCheck(itemEntity, false))
				|| Checker.isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), item)) {
			WeighedSoundEvents sound = Minecraft.getInstance().getSoundManager().getSoundEvent(LootBeams.LOOT_DROP);
			if(sound != null && Minecraft.getInstance().level != null) {
				Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvent.createFixedRangeEvent(LootBeams.LOOT_DROP, 8.0f), SoundSource.AMBIENT, 0.1f * Configuration.SOUND_VOLUME.get().floatValue(), 1.0f);
				playSoundCooldown = 3;
			}
		}
	}





}
