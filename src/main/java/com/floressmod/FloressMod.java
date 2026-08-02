package com.floressmod;

import com.floressmod.block.ModBlocks;
import com.floressmod.block.entity.ModBlockEntities;
import com.floressmod.compat.CarryOnCompat;
import com.floressmod.item.ModItemGroups;
import com.floressmod.item.ModItems;
import com.floressmod.world.DrainingWaterTicker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public final class FloressMod implements ModInitializer {
	public static final String MOD_ID = "floress_mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}



	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModBlockEntities.register();
		ModItems.register();
		ModItemGroups.register();
		DrainingWaterTicker.register();
		if (FabricLoader.getInstance().isModLoaded("carryon")) {
			CarryOnCompat.register();
		}
	}
}








