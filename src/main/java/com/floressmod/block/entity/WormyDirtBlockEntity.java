package com.floressmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public final class WormyDirtBlockEntity extends BlockEntity {
	private static final int PECK_SOUND_INTERVAL = 20;
	private static final int MIN_PECK_SECONDS = 5;
	private static final int MAX_PECK_SECONDS = 5;

	private int peckTicks;
	private int peckDuration = -1;
	private UUID peckerId;

	public WormyDirtBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.WORMY_DIRT, pos, state);
	}

	public boolean isFreeOrClaimedBy(ChickenEntity chicken) {
		return peckerId == null || peckerId.equals(chicken.getUuid());
	}

	public boolean tryClaim(ChickenEntity chicken) {
		if (peckerId != null && !peckerId.equals(chicken.getUuid())) {
			return false;
		}
		if (peckerId == null) {
			peckerId = chicken.getUuid();
			markDirty();
		}
		return true;
	}

	public void releaseClaim(ChickenEntity chicken) {
		if (peckerId != null && peckerId.equals(chicken.getUuid())) {
			peckerId = null;
			markDirty();
		}
	}

	public void peck(World world, BlockPos pos, ChickenEntity chicken) {
		if (!tryClaim(chicken)) {
			return;
		}

		if (peckDuration < 0) {
			peckDuration = 20 * (MIN_PECK_SECONDS + world.random.nextInt(MAX_PECK_SECONDS - MIN_PECK_SECONDS + 1));
		}

		peckTicks++;
		markDirty();

		if (peckTicks % PECK_SOUND_INTERVAL == 0) {
			world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 0.25F, 1.4F);
			world.playSound(null, pos, SoundEvents.BLOCK_GRASS_HIT, SoundCategory.BLOCKS, 0.25F, 1.2F);
		}

		if (peckTicks >= peckDuration) {
			chicken.heal(1.0F);
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT.value(), SoundCategory.NEUTRAL, 0.6F, 1.3F);
			world.setBlockState(pos, Blocks.SPRUCE_LOG.getDefaultState(), Block.NOTIFY_ALL);
			if (world instanceof ServerWorld serverWorld) {
				grantCleanReputation(serverWorld, pos);
			}
		}
	}

	private static void grantCleanReputation(ServerWorld world, BlockPos pos) {
		ServerPlayerEntity nearest = null;
		double nearestSq = Double.MAX_VALUE;
		net.minecraft.util.math.Vec3d center = net.minecraft.util.math.Vec3d.ofCenter(pos);
		for (ServerPlayerEntity player : net.fabricmc.fabric.api.networking.v1.PlayerLookup.around(
				world, center, com.floressmod.FloressConfig.WORM_CLEAN_REP_RADIUS)) {
			double distSq = player.squaredDistanceTo(center);
			if (distSq < nearestSq) {
				nearestSq = distSq;
				nearest = player;
			}
		}
		if (nearest != null) {
			com.floressmod.reputation.ReputationManager.add(nearest, com.floressmod.FloressConfig.REP_GAIN_WORMS_CLEANED);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		nbt.putInt("PeckTicks", peckTicks);
		nbt.putInt("PeckDuration", peckDuration);
		if (peckerId != null) {
			nbt.putUuid("Pecker", peckerId);
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		peckTicks = nbt.getInt("PeckTicks");
		peckDuration = nbt.contains("PeckDuration") ? nbt.getInt("PeckDuration") : -1;
		peckerId = nbt.containsUuid("Pecker") ? nbt.getUuid("Pecker") : null;
	}
}






