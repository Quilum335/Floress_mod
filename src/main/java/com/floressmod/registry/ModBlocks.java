package com.floressmod.registry;

import com.floressmod.FloressMod;
import com.floressmod.block.AmanitaBlock;
import com.floressmod.block.DeadLeavesBlock;
import com.floressmod.block.FruitBlock;
import com.floressmod.block.LyingBrickBlock;
import com.floressmod.block.PoisonIvyBlock;
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
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class ModBlocks {
	private ModBlocks() {}

	/** Земля с червями — курицы её клюют, превращая в обычную землю. */
	public static final Block WORMY_DIRT = register("wormy_dirt", WormyDirtBlock::new,
			AbstractBlock.Settings.copy(Blocks.DIRT));

	/** Просто лежащий кирпич — при разрушении даёт кирпич. */
	public static final Block LOOSE_BRICK = register("loose_brick", LyingBrickBlock::new,
			AbstractBlock.Settings.copy(Blocks.BRICKS).nonOpaque().noCollision());

	/** Ядовитый плющ (ползучий, как лиана). Слом = сильная потеря репутации. */
	public static final Block POISON_IVY = register("poison_ivy", PoisonIvyBlock::new,
			AbstractBlock.Settings.copy(Blocks.VINE));

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
		RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FloressMod.MOD_ID, name));
		Block block = factory.apply(settings.registryKey(key));
		return Registry.register(Registries.BLOCK, key, block);
	}

	public static void register() {
		// класс инициализируется статикой
	}
}
