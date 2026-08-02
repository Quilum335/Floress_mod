package com.floressmod.block.entity;

import com.floressmod.FloressMod;
import com.floressmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
	public static final BlockEntityType<WormyDirtBlockEntity> WORMY_DIRT = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			FloressMod.id("wormy_dirt"),
			FabricBlockEntityTypeBuilder.create(WormyDirtBlockEntity::new, ModBlocks.WORMY_DIRT).build());

	private ModBlockEntities() {
	}

	public static void register() {
	}
}






