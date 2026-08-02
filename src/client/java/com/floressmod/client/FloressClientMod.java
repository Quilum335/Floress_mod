package com.floressmod.client;

import com.floressmod.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.world.biome.GrassColors;

public final class FloressClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlocks(
				RenderLayer.getCutoutMipped(),
				ModBlocks.SOFT_TURF,
				ModBlocks.PLUSCH,
				Blocks.RED_MUSHROOM,
				Blocks.POTTED_RED_MUSHROOM
		);

		ColorProviderRegistry.BLOCK.register(
				(state, world, pos, tintIndex) -> {
					if (world == null || pos == null) {
						return GrassColors.getDefaultColor();
					}
					return BiomeColors.getGrassColor(world, pos);
				},
				ModBlocks.SOFT_TURF
		);
	}
}
