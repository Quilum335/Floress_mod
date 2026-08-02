package com.floressmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public final class WormyDirtBlockEntity extends BlockEntity {
	private static final int PECK_SOUND_INTERVAL = 25;
	private static final int MIN_PECK_SECONDS = 20;
	private static final int MAX_PECK_SECONDS = 30;

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
			world.setBlockState(pos, Blocks.DIRT.getDefaultState(), Block.NOTIFY_ALL);
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






