package com.floressmod.item;

import com.floressmod.FloressMod;
import com.floressmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class ModItemGroups {
	public static final ItemGroup FLORESS = Registry.register(
			Registries.ITEM_GROUP,
			FloressMod.id("floress"),
			FabricItemGroup.builder()
					.icon(() -> new ItemStack(ModItems.WORM))
					.displayName(Text.translatable("itemGroup.floress_mod.floress"))
					.entries((displayContext, entries) -> {
						entries.add(ModItems.WORM);
						entries.add(ModItems.WORMY_DIRT);
						entries.add(ModItems.SOFT_TURF);
						entries.add(ModItems.LYING_BRICK);
						entries.add(ModItems.PLUSCH);
						entries.add(ModItems.POTTED_RED_MUSHROOM);
					})
					.build()
	);

	private ModItemGroups() {
	}

	public static void register() {
	}
}