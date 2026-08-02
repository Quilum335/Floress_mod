package com.floressmod.item;

import com.floressmod.FloressMod;
import com.floressmod.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {
	public static final Item WORM = register("worm", new Item(new Item.Settings()
			.registryKey(RegistryKey.of(RegistryKeys.ITEM, FloressMod.id("worm")))));

	public static final Item WORMY_DIRT = register("wormy_dirt", new BlockItem(
			ModBlocks.WORMY_DIRT,
			new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, FloressMod.id("wormy_dirt")))));

	public static final Item SOFT_TURF = register("soft_turf", new BlockItem(
			ModBlocks.SOFT_TURF,
			new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, FloressMod.id("soft_turf")))));

	public static final Item LYING_BRICK = register("lying_brick", new BlockItem(
			ModBlocks.LYING_BRICK,
			new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, FloressMod.id("lying_brick")))));

	public static final Item PLUSCH = register("plusch", new BlockItem(
			ModBlocks.PLUSCH,
			new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, FloressMod.id("plusch")))));

	public static final Item POTTED_RED_MUSHROOM = registerBlockItem("potted_red_mushroom", Blocks.POTTED_RED_MUSHROOM);

	private ModItems() {
	}

	private static Item register(String path, Item item) {
		return Registry.register(Registries.ITEM, FloressMod.id(path), item);
	}

	private static Item registerBlockItem(String path, Block block) {
		Identifier id = FloressMod.id(path);
		BlockItem item = new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
		Item registered = Registry.register(Registries.ITEM, id, item);
		item.appendBlocks(Item.BLOCK_ITEMS, registered);
		return registered;
	}

	public static void register() {
	}
}