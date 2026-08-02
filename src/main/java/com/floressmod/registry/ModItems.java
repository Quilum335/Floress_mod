package com.floressmod.registry;

import com.floressmod.FloressMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItems {
	private ModItems() {}

	/** Червь — еда для размножения куриц (тег minecraft:chicken_food). */
	public static final Item WORM = registerItem("worm", new Item(new Item.Settings()
			.food(new FoodComponent.Builder().nutrition(1).saturationModifier(0.1f).build())
			.registryKey(itemKey("worm"))));

	public static final Item WORMY_DIRT = registerBlockItem("wormy_dirt", ModBlocks.WORMY_DIRT);
	public static final Item LOOSE_BRICK = registerBlockItem("loose_brick", ModBlocks.LOOSE_BRICK);
	public static final Item POISON_IVY = registerBlockItem("poison_ivy", ModBlocks.POISON_IVY);
	public static final Item DEAD_LEAVES = registerBlockItem("dead_leaves", ModBlocks.DEAD_LEAVES);
	public static final Item AMANITA = registerBlockItem("amanita", ModBlocks.AMANITA);

	public static final Item LIVING_MUSHROOM_SPAWN_EGG = registerItem("living_mushroom_spawn_egg",
			new SpawnEggItem(ModEntities.LIVING_MUSHROOM,
					new Item.Settings().registryKey(itemKey("living_mushroom_spawn_egg"))));
	public static final Item FLY_SPAWN_EGG = registerItem("fly_spawn_egg",
			new SpawnEggItem(ModEntities.FLY,
					new Item.Settings().registryKey(itemKey("fly_spawn_egg"))));

	public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP,
			Identifier.of(FloressMod.MOD_ID, "main"),
			FabricItemGroup.builder()
					.icon(() -> new ItemStack(AMANITA))
					.displayName(Text.translatable("itemGroup.floress_mod"))
					.entries((context, entries) -> {
						entries.add(WORM);
						entries.add(WORMY_DIRT);
						entries.add(LOOSE_BRICK);
						entries.add(POISON_IVY);
						entries.add(DEAD_LEAVES);
						entries.add(AMANITA);
						entries.add(LIVING_MUSHROOM_SPAWN_EGG);
						entries.add(FLY_SPAWN_EGG);
					})
					.build());

	private static RegistryKey<Item> itemKey(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(FloressMod.MOD_ID, name));
	}

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, itemKey(name), item);
	}

	private static Item registerBlockItem(String name, net.minecraft.block.Block block) {
		RegistryKey<Item> key = itemKey(name);
		return Registry.register(Registries.ITEM, key,
				new BlockItem(block, new Item.Settings().registryKey(key).useBlockPrefixedTranslationKey()));
	}

	public static void register() {
		// статика
	}
}
