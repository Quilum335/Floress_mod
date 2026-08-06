package com.floressmod.registry;

import com.floressmod.FloressMod;
import com.floressmod.block.FruitBlock;
import com.floressmod.item.FruitStageBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

public final class ModItems {
	private ModItems() {}

	/** Червяк — еда для размножения куриц (тег minecraft:chicken_food). */
	public static final Item WORM = registerItem("worm", new Item(new Item.Settings()
			.food(new FoodComponent.Builder().nutrition(1).saturationModifier(0.1f).build())
			.registryKey(itemKey("worm"))));

	public static final Item WORMY_DIRT = registerBlockItem("wormy_dirt", ModBlocks.WORMY_DIRT);
	public static final Item SOFT_TURF = registerBlockItem("soft_turf", ModBlocks.SOFT_TURF);
	public static final Item DIRT = registerBlockItem("dirt", ModBlocks.DIRT);
	public static final Item LYING_BRICK = registerBlockItem("lying_brick", ModBlocks.LYING_BRICK);
	public static final Item PLUSCH = registerBlockItem("plusch", ModBlocks.PLUSCH);
	public static final Item LEAVES = registerBlockItem("leaves", ModBlocks.LEAVES);
	public static final Item DEAD_LEAVES = registerBlockItem("dead_leaves", ModBlocks.DEAD_LEAVES);
	public static final Item AMANITA = registerBlockItem("amanita", ModBlocks.AMANITA);

	/** Алиас-предмет для ванильного мухомора в горшке — у него своя модель в assets. */
	public static final Item POTTED_RED_MUSHROOM = registerAliasedBlockItem("potted_red_mushroom", Blocks.POTTED_RED_MUSHROOM);

	/** Плод дерева — ставится под листву спелым, со случайным типом (для тестов/съёмок). */
	public static final Item FRUIT = registerBlockItem("fruit", ModBlocks.FRUIT);

	/** Плоды трёх стадий роста — для постановки конкретной стадии рукой (тип случайный). */
	public static final Item FRUIT_STAGE_1 = registerItem("fruit_stage_1",
			new FruitStageBlockItem(ModBlocks.FRUIT,
					new Item.Settings().registryKey(itemKey("fruit_stage_1")), 0));
	public static final Item FRUIT_STAGE_2 = registerItem("fruit_stage_2",
			new FruitStageBlockItem(ModBlocks.FRUIT,
					new Item.Settings().registryKey(itemKey("fruit_stage_2")), 2));
	public static final Item FRUIT_STAGE_3 = registerItem("fruit_stage_3",
			new FruitStageBlockItem(ModBlocks.FRUIT,
					new Item.Settings().registryKey(itemKey("fruit_stage_3")), FruitBlock.RIPE_AGE + 1));

	/** Трутовик — крепится на стены и потолок. */
	public static final Item SHELF_MUSHROOM = registerBlockItem("shelf_mushroom", ModBlocks.SHELF_MUSHROOM);

	public static final Item LIVING_MUSHROOM_SPAWN_EGG = registerItem("living_mushroom_spawn_egg",
			new SpawnEggItem(ModEntities.LIVING_MUSHROOM,
					new Item.Settings().registryKey(itemKey("living_mushroom_spawn_egg"))));
	public static final Item FLY_SPAWN_EGG = registerItem("fly_spawn_egg",
			new SpawnEggItem(ModEntities.FLY,
					new Item.Settings().registryKey(itemKey("fly_spawn_egg"))));

	public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP,
			FloressMod.id("main"),
			FabricItemGroup.builder()
					.icon(() -> new ItemStack(AMANITA))
					.displayName(Text.translatable("itemGroup.floress_mod"))
					.entries((context, entries) -> {
						entries.add(WORM);
						entries.add(WORMY_DIRT);
						entries.add(SOFT_TURF);
						entries.add(DIRT);
						entries.add(LYING_BRICK);
						entries.add(PLUSCH);
						entries.add(LEAVES);
						entries.add(DEAD_LEAVES);
						entries.add(AMANITA);
						entries.add(POTTED_RED_MUSHROOM);
						entries.add(FRUIT);
						entries.add(FRUIT_STAGE_1);
						entries.add(FRUIT_STAGE_2);
						entries.add(FRUIT_STAGE_3);
						entries.add(SHELF_MUSHROOM);
						entries.add(LIVING_MUSHROOM_SPAWN_EGG);
						entries.add(FLY_SPAWN_EGG);
					})
					.build());

	private static RegistryKey<Item> itemKey(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, FloressMod.id(name));
	}

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, itemKey(name), item);
	}

	private static Item registerBlockItem(String name, Block block) {
		RegistryKey<Item> key = itemKey(name);
		return Registry.register(Registries.ITEM, key,
				new BlockItem(block, new Item.Settings().registryKey(key).useBlockPrefixedTranslationKey()));
	}

	private static Item registerAliasedBlockItem(String name, Block block) {
		RegistryKey<Item> key = itemKey(name);
		BlockItem item = new BlockItem(block, new Item.Settings().registryKey(key));
		Item registered = Registry.register(Registries.ITEM, key, item);
		item.appendBlocks(Item.BLOCK_ITEMS, registered);
		return registered;
	}

	public static void register() {
		// статика
	}
}
