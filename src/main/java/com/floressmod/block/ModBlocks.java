package com.floressmod.block;

import com.floressmod.FloressMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public final class ModBlocks {
	public static final Block WORMY_DIRT = register("wormy_dirt", new WormyDirtBlock(
			AbstractBlock.Settings.copy(Blocks.DIRT)
					.registryKey(RegistryKey.of(RegistryKeys.BLOCK, FloressMod.id("wormy_dirt")))));

	public static final Block SOFT_TURF = register("soft_turf", new SoftTurfBlock(
			AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK)
					.registryKey(RegistryKey.of(RegistryKeys.BLOCK, FloressMod.id("soft_turf")))));

	
	public static final Block LYING_BRICK = register("lying_brick", new LyingBrickBlock(
			AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_RED)
					.strength(0.5F)
					.sounds(BlockSoundGroup.STONE)
					.nonOpaque()
					.pistonBehavior(PistonBehavior.DESTROY)
					.registryKey(RegistryKey.of(RegistryKeys.BLOCK, FloressMod.id("lying_brick")))));

	
	
	public static final Block PLUSCH = register("plusch", new PluschBlock(
			AbstractBlock.Settings.create()
					.mapColor(MapColor.DARK_GREEN)
					.breakInstantly()
					.noCollision()
					.sounds(BlockSoundGroup.GRASS)
					.nonOpaque()
					.replaceable()
					.pistonBehavior(PistonBehavior.DESTROY)
					.registryKey(RegistryKey.of(RegistryKeys.BLOCK, FloressMod.id("plusch")))));

	private ModBlocks() {
	}



	private static Block register(String path, Block block) {
		return Registry.register(Registries.BLOCK, FloressMod.id(path), block);
	}

	public static void register() {
	}
}





