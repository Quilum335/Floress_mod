package com.floressmod;

import com.floressmod.mechanics.PendingBlockRemovals;
import com.floressmod.mechanics.WaterBottleDepletion;
import com.floressmod.registry.ModBlocks;
import com.floressmod.registry.ModEntities;
import com.floressmod.registry.ModItems;
import com.floressmod.reputation.ModCommands;
import com.floressmod.reputation.ReputationEvents;
import com.floressmod.reputation.ReputationNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloressMod implements ModInitializer {
	public static final String MOD_ID = "floress_mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModEntities.register();
		ModItems.register();
		ReputationNetworking.registerServer();
		ReputationEvents.register();
		ModCommands.register();
		WaterBottleDepletion.register();
		PendingBlockRemovals.register();
		LOGGER.info("Floress Mod initialized");
	}
}
