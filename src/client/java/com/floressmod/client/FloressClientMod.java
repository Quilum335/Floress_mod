package com.floressmod.client;

import com.floressmod.registry.ModBlocks;
import com.floressmod.registry.ModEntities;
import com.floressmod.reputation.ReputationSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.world.biome.GrassColors;

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

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
				ModBlocks.DEAD_LEAVES, ModBlocks.PLUSCH, ModBlocks.AMANITA, ModBlocks.FRUIT,
				ModBlocks.SOFT_TURF, Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM);

		ColorProviderRegistry.BLOCK.register(
				(state, world, pos, tintIndex) -> {
					if (world == null || pos == null) {
						return GrassColors.getDefaultColor();
					}
					return BiomeColors.getGrassColor(world, pos);
				},
				ModBlocks.SOFT_TURF);
	}
}
