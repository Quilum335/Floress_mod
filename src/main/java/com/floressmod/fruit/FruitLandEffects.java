package com.floressmod.fruit;

import com.floressmod.FloressConfig;
import com.floressmod.block.AmanitaBlock;
import com.floressmod.entity.FlyEntity;
import com.floressmod.entity.LivingMushroomEntity;
import com.floressmod.registry.ModBlocks;
import com.floressmod.registry.ModEntities;
import com.floressmod.registry.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

/**
 * То, что происходит при падении плода. У всех плодов одинаковое
 * «начало» (зреют, падают), различается только этот эффект.
 */
public final class FruitLandEffects {
	private FruitLandEffects() {}

	public static void trigger(ServerWorld world, BlockPos pos, FruitType type) {
		spawnLandingParticles(world, pos, type.isGood());
		switch (type) {
			case FLY -> spawnFlies(world, pos);
			case HARVEST -> dropHarvest(world, pos);
			case EXPLOSIVE -> explode(world, pos);
			case RABBIT -> spawnRabbits(world, pos);
			case ZOMBIE -> spawnZombies(world, pos);
			case CHICKEN -> spawnChickensAndSaplings(world, pos);
		}
	}

	private static void spawnLandingParticles(ServerWorld world, BlockPos pos, boolean good) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		if (good) {
			world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 20, 0.6, 0.6, 0.6, 0.05);
			world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0f, 1.2f);
		} else {
			world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 12, 0.6, 0.6, 0.6, 0.05);
			world.spawnParticles(ParticleTypes.WITCH, x, y, z, 15, 0.5, 0.5, 0.5, 0.02);
			world.playSound(null, pos, SoundEvents.ENTITY_WITCH_AMBIENT, SoundCategory.HOSTILE, 1.0f, 0.8f);
		}
	}

	/** 1) Мухи — агрессивные, нападают на игрока. */
	private static void spawnFlies(ServerWorld world, BlockPos pos) {
		int count = 3 + world.random.nextInt(3); // 3–5
		for (int i = 0; i < count; i++) {
			FlyEntity fly = ModEntities.FLY.create(world);
			if (fly == null) continue;
			fly.refreshPositionAndAngles(
					pos.getX() + 0.5 + world.random.nextGaussian(),
					pos.getY() + 0.5,
					pos.getZ() + 0.5 + world.random.nextGaussian(),
					world.random.nextFloat() * 360.0f, 0.0f);
			world.spawnEntity(fly);
		}
	}

	/** 2) Урожай — картошка и морковь. */
	private static void dropHarvest(ServerWorld world, BlockPos pos) {
		Block.dropStack(world, pos, new ItemStack(Items.POTATO, 2 + world.random.nextInt(3)));
		Block.dropStack(world, pos, new ItemStack(Items.CARROT, 2 + world.random.nextInt(3)));
		world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8f, 0.9f);
	}

	/** 3) Бомба — ломает блоки КРОМЕ природных, мухоморы становятся живыми. */
	private static void explode(ServerWorld world, BlockPos pos) {
		ExplosionBehavior behavior = new ExplosionBehavior() {
			@Override
			public boolean canDestroyBlock(Explosion explosion, net.minecraft.world.BlockView view, BlockPos blockPos, BlockState state, float power) {
				// брёвна, дёрн/земля, листва и вообще всё природное — не трогаем
				return !state.isIn(ModTags.EXPLOSION_NATURAL);
			}
		};
		world.createExplosion(null, null, behavior, Vec3d.ofCenter(pos),
				FloressConfig.FRUIT_EXPLOSION_POWER, false, World.ExplosionSourceType.BLOCK);

		// мухоморы в радиусе превращаются в ЖИВЫХ грибов
		int r = FloressConfig.FRUIT_EXPLOSION_AMANITA_RADIUS;
		int converted = 0;
		for (BlockPos check : BlockPos.iterate(pos.add(-r, -r, -r), pos.add(r, r, r))) {
			if (converted >= 12) break;
			if (world.getBlockState(check).getBlock() instanceof AmanitaBlock) {
				world.removeBlock(check, false);
				LivingMushroomEntity mushroom = ModEntities.LIVING_MUSHROOM.create(world);
				if (mushroom != null) {
					mushroom.refreshPositionAndAngles(check.getX() + 0.5, check.getY(), check.getZ() + 0.5,
							world.random.nextFloat() * 360.0f, 0.0f);
					world.spawnEntity(mushroom);
					converted++;
				}
			}
		}
	}

	/** 4) Кролики. */
	private static void spawnRabbits(ServerWorld world, BlockPos pos) {
		int count = 2 + world.random.nextInt(2); // 2–3
		for (int i = 0; i < count; i++) {
			RabbitEntity rabbit = EntityType.RABBIT.create(world);
			if (rabbit == null) continue;
			rabbit.refreshPositionAndAngles(
					pos.getX() + 0.5 + world.random.nextGaussian(),
					pos.getY() + 0.5,
					pos.getZ() + 0.5 + world.random.nextGaussian(),
					world.random.nextFloat() * 360.0f, 0.0f);
			world.spawnEntity(rabbit);
		}
	}

	/** 5) Зомби в железной броне (не фул сет) + живые грибы. */
	private static void spawnZombies(ServerWorld world, BlockPos pos) {
		EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		ItemStack[] ironArmor = {
				new ItemStack(Items.IRON_HELMET),
				new ItemStack(Items.IRON_CHESTPLATE),
				new ItemStack(Items.IRON_LEGGINGS),
				new ItemStack(Items.IRON_BOOTS)
		};
		for (int i = 0; i < 2; i++) {
			ZombieEntity zombie = EntityType.ZOMBIE.create(world);
			if (zombie == null) continue;
			zombie.refreshPositionAndAngles(
					pos.getX() + 0.5 + world.random.nextGaussian(),
					pos.getY() + 0.5,
					pos.getZ() + 0.5 + world.random.nextGaussian(),
					world.random.nextFloat() * 360.0f, 0.0f);
			// не фул сет: 2 случайных элемента брони
			int first = world.random.nextInt(armorSlots.length);
			int second = (first + 1 + world.random.nextInt(armorSlots.length - 1)) % armorSlots.length;
			zombie.equipStack(armorSlots[first], ironArmor[first].copy());
			zombie.equipStack(armorSlots[second], ironArmor[second].copy());
			world.spawnEntity(zombie);
		}
		int mushrooms = 1 + world.random.nextInt(2); // 1–2
		for (int i = 0; i < mushrooms; i++) {
			LivingMushroomEntity mushroom = ModEntities.LIVING_MUSHROOM.create(world);
			if (mushroom == null) continue;
			mushroom.refreshPositionAndAngles(
					pos.getX() + 0.5 + world.random.nextGaussian(),
					pos.getY() + 0.5,
					pos.getZ() + 0.5 + world.random.nextGaussian(),
					world.random.nextFloat() * 360.0f, 0.0f);
			world.spawnEntity(mushroom);
		}
	}

	/** 6) Курицы + саженцы случайных деревьев. */
	private static void spawnChickensAndSaplings(ServerWorld world, BlockPos pos) {
		int chickens = 2 + world.random.nextInt(3); // 2–4
		for (int i = 0; i < chickens; i++) {
			ChickenEntity chicken = EntityType.CHICKEN.create(world);
			if (chicken == null) continue;
			chicken.refreshPositionAndAngles(
					pos.getX() + 0.5 + world.random.nextGaussian(),
					pos.getY() + 0.5,
					pos.getZ() + 0.5 + world.random.nextGaussian(),
					world.random.nextFloat() * 360.0f, 0.0f);
			world.spawnEntity(chicken);
		}

		Block[] saplings = {
				Blocks.OAK_SAPLING, Blocks.BIRCH_SAPLING, Blocks.SPRUCE_SAPLING,
				Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING,
				Blocks.CHERRY_SAPLING
		};
		int planted = 0;
		int target = 3 + world.random.nextInt(3); // 3–5 саженцев
		for (int attempt = 0; attempt < 24 && planted < target; attempt++) {
			int dx = world.random.nextInt(7) - 3;
			int dz = world.random.nextInt(7) - 3;
			BlockPos ground = findGround(world, pos.add(dx, 2, dz));
			if (ground == null) continue;
			Block sapling = saplings[world.random.nextInt(saplings.length)];
			if (sapling.getDefaultState().canPlaceAt(world, ground.up())) {
				world.setBlockState(ground.up(), sapling.getDefaultState());
				planted++;
			}
		}
	}

	/** Ищет ближайший блок земли/дерна сверху вниз в маленьком диапазоне. */
	private static BlockPos findGround(ServerWorld world, BlockPos from) {
		BlockPos.Mutable cursor = from.mutableCopy();
		for (int i = 0; i < 5; i++) {
			BlockState state = world.getBlockState(cursor);
			if (state.isIn(BlockTags.DIRT) && world.getBlockState(cursor.up()).isAir()) {
				return cursor.toImmutable();
			}
			cursor.move(0, -1, 0);
		}
		return null;
	}
}
