package com.floressmod.registry;

import com.floressmod.FloressMod;
import com.floressmod.entity.FallingFruitEntity;
import com.floressmod.entity.FlyEntity;
import com.floressmod.entity.LivingMushroomEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModEntities {
	private ModEntities() {}

	public static final EntityType<LivingMushroomEntity> LIVING_MUSHROOM = register("living_mushroom",
			EntityType.Builder.create(LivingMushroomEntity::new, SpawnGroup.MONSTER)
					.dimensions(0.6f, 0.9f)
					.eyeHeight(0.7f)
					.maxTrackingRange(8));

	public static final EntityType<FlyEntity> FLY = register("fly",
			EntityType.Builder.create(FlyEntity::new, SpawnGroup.MONSTER)
					.dimensions(0.4f, 0.3f)
					.eyeHeight(0.2f)
					.maxTrackingRange(8));

	public static final EntityType<FallingFruitEntity> FALLING_FRUIT = register("falling_fruit",
			EntityType.Builder.create(FallingFruitEntity::new, SpawnGroup.MISC)
					.dimensions(0.5f, 0.5f)
					.maxTrackingRange(10)
					.trackingTickInterval(20));

	private static <T extends net.minecraft.entity.Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FloressMod.MOD_ID, name));
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void register() {
		FabricDefaultAttributeRegistry.register(LIVING_MUSHROOM, LivingMushroomEntity.createLivingMushroomAttributes());
		FabricDefaultAttributeRegistry.register(FLY, FlyEntity.createFlyAttributes());
	}
}
