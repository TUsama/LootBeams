package com.lootbeams;

import com.lootbeams.events.LBEventBus;
import com.lootbeams.modules.ModulesManager;
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


	}


}
