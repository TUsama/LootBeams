package com.clefal.lootbeams;

import com.clefal.lootbeams.events.LBEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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

		getModLoadingContext().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		getModLoadingContext().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);

		//FMLJavaModLoadingContext.get().getModEventBus().addListener(com.lootbeams.ClientSetup::init);


	}

	public static ModLoadingContext getModLoadingContext(){
		return ModLoadingContext.get();
	}

}
