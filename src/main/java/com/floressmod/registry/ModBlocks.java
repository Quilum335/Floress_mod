package com.floressmod.registry;

import com.floressmod.FloressMod;
import com.floressmod.block.AmanitaBlock;
import com.floressmod.block.DeadLeavesBlock;
import com.floressmod.block.FruitBlock;
import com.floressmod.block.LyingBrickBlock;
import com.floressmod.block.PluschBlock;
import com.floressmod.block.SoftTurfBlock;
import com.floressmod.block.WormyDirtBlock;
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

import java.util.function.Function;

public final class ModBlocks {
	private ModBlocks() {}

	/** Дёрн с червями — курицы его клюют, превращая в обычную землю (BlockEntity с прогрессом). */
	public static final Block WORMY_DIRT = register("wormy_dirt", WormyDirtBlock::new,
			AbstractBlock.Settings.copy(Blocks.DIRT));

	/** Мягкий дёрн — вытаптывается со временем (износ 0-3), потом становится землёй. */
	public static final Block SOFT_TURF = register("soft_turf", SoftTurfBlock::new,
			AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK));

	/** Лежащий кирпич — при разрушении даёт кирпич. */
	public static final Block LYING_BRICK = register("lying_brick", LyingBrickBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.TERRACOTTA_RED)
					.strength(0.5F)
					.sounds(BlockSoundGroup.STONE)
					.nonOpaque()
					.pistonBehavior(PistonBehavior.DESTROY));

	/** Плющ — ядовитый куст: замедляет, ранит и травит ядом. Слом = сильная потеря репутации. */
	public static final Block PLUSCH = register("plusch", PluschBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.DARK_GREEN)
					.breakInstantly()
					.noCollision()
					.sounds(BlockSoundGroup.GRASS)
					.nonOpaque()
					.replaceable()
					.pistonBehavior(PistonBehavior.DESTROY));

	/** Мёртвая листва. Слом = рост репутации. */
	public static final Block DEAD_LEAVES = register("dead_leaves", DeadLeavesBlock::new,
			AbstractBlock.Settings.copy(Blocks.OAK_LEAVES));

	/** Мухомор. На мицелии размножается костной мукой. */
	public static final Block AMANITA = register("amanita", AmanitaBlock::new,
			AbstractBlock.Settings.copy(Blocks.RED_MUSHROOM));

	/** Плод дерева — зреет 5 дней, падает, вызывает ивент. Предметной формы нет. */
	public static final Block FRUIT = register("fruit", FruitBlock::new,
			AbstractBlock.Settings.create()
					.mapColor(MapColor.GREEN)
					.sounds(BlockSoundGroup.CROP)
					.noCollision()
					.breakInstantly()
					.nonOpaque()
					.pistonBehavior(PistonBehavior.DESTROY));

	private static Block register(String name, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
		RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, FloressMod.id(name));
		Block block = factory.apply(settings.registryKey(key));
		return Registry.register(Registries.BLOCK, key, block);
	}

	public static void register() {
		// класс инициализируется статикой
	}
}
