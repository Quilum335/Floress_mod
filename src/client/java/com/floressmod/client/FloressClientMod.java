package com.floressmod.client;

import com.floressmod.registry.ModBlocks;
import com.floressmod.registry.ModEntities;
import com.floressmod.reputation.ReputationSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;

public class FloressClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ReputationSyncPayload.ID,
				(payload, context) -> ClientReputation.set(payload.value()));

		ReputationHud.register();
		ModEntityModelLayers.register();
		ModelLoadingPlugin.register(context -> context.addModels(
				FallingFruitRenderer.BROKEN_LEFT_MODEL,
				FallingFruitRenderer.BROKEN_RIGHT_MODEL));

		EntityRendererRegistry.register(ModEntities.LIVING_MUSHROOM, LivingMushroomRenderer::new);
		EntityRendererRegistry.register(ModEntities.FLY, FlyRenderer::new);
		EntityRendererRegistry.register(ModEntities.FALLING_FRUIT, FallingFruitRenderer::new);

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
				ModBlocks.LEAVES, ModBlocks.DEAD_LEAVES, ModBlocks.PLUSCH, ModBlocks.AMANITA, ModBlocks.FRUIT,
				ModBlocks.SOFT_TURF, Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM);
	}
}
