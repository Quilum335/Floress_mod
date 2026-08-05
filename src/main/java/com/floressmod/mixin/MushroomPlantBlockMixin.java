package com.floressmod.mixin;

import com.floressmod.block.AmanitaBlock;
import com.floressmod.block.MushroomBounce;
import com.floressmod.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MushroomPlantBlock.class)
public abstract class MushroomPlantBlockMixin {
	@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
	private void floress$placeOnSoftTurf(
			BlockState state,
			WorldView world,
			BlockPos pos,
			CallbackInfoReturnable<Boolean> cir
	) {
		BlockState floor = world.getBlockState(pos.down());
		if (floor.isOf(ModBlocks.SOFT_TURF)
				|| floor.isOf(ModBlocks.DIRT)
				|| floor.isOf(ModBlocks.WORMY_DIRT)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	private void floress$noNaturalSpread(
			BlockState state,
			ServerWorld world,
			BlockPos pos,
			Random random,
			CallbackInfo ci
	) {
		ci.cancel();
	}

	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	private void floress$redMushroomOutline(
			BlockState state,
			BlockView world,
			BlockPos pos,
			ShapeContext context,
			CallbackInfoReturnable<VoxelShape> cir
	) {
		if (MushroomBounce.isBounceBlock(state) && !state.isOf(Blocks.POTTED_RED_MUSHROOM)) {
			cir.setReturnValue(MushroomBounce.RED_MUSHROOM_OUTLINE);
		}
	}

	@Inject(method = "canGrow", at = @At("HEAD"), cancellable = true)
	private void floress$alwaysOnMycelium(
			World world,
			Random random,
			BlockPos pos,
			BlockState state,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (!world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM)) {
			return;
		}
		if (state.isOf(Blocks.RED_MUSHROOM) || (Object) this instanceof AmanitaBlock) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "grow", at = @At("HEAD"), cancellable = true)
	private void floress$duplicateOnMycelium(
			ServerWorld world,
			Random random,
			BlockPos pos,
			BlockState state,
			CallbackInfo ci
	) {
		if (!world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM)) {
			return;
		}
		if (state.isOf(Blocks.RED_MUSHROOM)) {
			Block.dropStack(world, pos, new ItemStack(Blocks.RED_MUSHROOM));
			ci.cancel();
			return;
		}
		if ((Object) this instanceof AmanitaBlock amanita) {
			Block.dropStack(world, pos, new ItemStack(amanita));
			ci.cancel();
		}
	}
}