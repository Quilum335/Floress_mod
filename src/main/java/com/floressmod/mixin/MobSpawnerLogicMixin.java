package com.floressmod.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobSpawnerLogic.class)
public abstract class MobSpawnerLogicMixin {
	@Unique
	private static final int floress$MAX_TRIGGERS = 5;

	@Unique
	private int floress$triggerCount;

	@Unique
	private boolean floress$countedThisCycle;

	@Inject(method = "serverTick", at = @At("HEAD"))
	private void floress$resetCycle(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		floress$countedThisCycle = false;
	}

	@Inject(
			method = "serverTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerWorld;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V"
			)
	)
	private void floress$onSpawnTrigger(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		if (floress$countedThisCycle) {
			return;
		}
		floress$countedThisCycle = true;
		floress$triggerCount++;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			blockEntity.markDirty();
		}

		if (floress$triggerCount < floress$MAX_TRIGGERS) {
			return;
		}

		BlockState state = world.getBlockState(pos);
		world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
		world.spawnParticles(
				ParticleTypes.POOF,
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5,
				18,
				0.35,
				0.35,
				0.35,
				0.02
		);
		world.spawnParticles(
				ParticleTypes.SMOKE,
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5,
				12,
				0.3,
				0.3,
				0.3,
				0.01
		);
		world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.8F, 0.9F);
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Inject(method = "readNbt", at = @At("TAIL"))
	private void floress$readTriggers(World world, BlockPos pos, NbtCompound nbt, CallbackInfo ci) {
		floress$triggerCount = nbt.getInt("FloressTriggerCount");
	}

	@Inject(method = "writeNbt", at = @At("TAIL"))
	private void floress$writeTriggers(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
		nbt.putInt("FloressTriggerCount", floress$triggerCount);
	}
}