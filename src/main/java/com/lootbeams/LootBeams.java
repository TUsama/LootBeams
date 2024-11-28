package com.lootbeams;

import com.lootbeams.events.LBEventBus;
import com.lootbeams.modules.ModulesManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import net.neoforged.bus.BusBuilderImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(LootBeams.MODID)
public class LootBeams {

	public static final String MODID = "lootbeams";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final List<ItemStack> CRASH_BLACKLIST = new ArrayList<>();
	public static final ResourceLocation LOOT_DROP = new ResourceLocation(MODID, "loot_drop");
	public static final LBEventBus EVENT_BUS = new LBEventBus(new BusBuilderImpl());

	public LootBeams() {

		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, com.lootbeams.Configuration.CLIENT_CONFIG);

		//FMLJavaModLoadingContext.get().getModEventBus().addListener(com.lootbeams.ClientSetup::init);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ModulesManager::registerAll);

	}


	public static RenderType translucentNoCull(ResourceLocation texture) {
		return RenderType.create("lootbeams_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setWriteMaskState(RenderStateShard.COLOR_WRITE).setCullState(RenderStateShard.NO_CULL).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(false));
	}
}
