package com.floressmod.client;

import com.floressmod.registry.ModEntities;
import com.floressmod.reputation.ReputationSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class FloressClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ReputationSyncPayload.ID,
				(payload, context) -> ClientReputation.set(payload.value()));

		ReputationHud.register();
		ModEntityModelLayers.register();

		EntityRendererRegistry.register(ModEntities.LIVING_MUSHROOM, LivingMushroomRenderer::new);
		EntityRendererRegistry.register(ModEntities.FLY, FlyRenderer::new);
		EntityRendererRegistry.register(ModEntities.FALLING_FRUIT, FallingFruitRenderer::new);
	}
}
