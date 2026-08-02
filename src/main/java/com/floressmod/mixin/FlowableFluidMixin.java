package com.floressmod.mixin;

import com.floressmod.world.WaterBottleState;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {
	@Inject(method = "onScheduledTick", at = @At("HEAD"), cancellable = true)
	private void floress$freezeBottledWater(
			ServerWorld world,
			BlockPos pos,
			BlockState blockState,
			FluidState state,
			CallbackInfo ci
	) {
		if (state.isIn(FluidTags.WATER) && WaterBottleState.isDraining(world, pos)) {
			ci.cancel();
		}
	}

	@Inject(method = "flow", at = @At("HEAD"), cancellable = true)
	private void floress$keepBottledWater(
			WorldAccess world,
			BlockPos pos,
			BlockState state,
			Direction direction,
			FluidState fluidState,
			CallbackInfo ci
	) {
		if (world instanceof ServerWorld serverWorld && WaterBottleState.isDraining(serverWorld, pos)) {
			ci.cancel();
		}
	}

	@Inject(method = "tryFlow", at = @At("HEAD"), cancellable = true)
	private void floress$noFlowFromBottledWater(
			ServerWorld world,
			BlockPos pos,
			BlockState blockState,
			FluidState fluidState,
			CallbackInfo ci
	) {
		if (fluidState.isIn(FluidTags.WATER) && WaterBottleState.isDraining(world, pos)) {
			ci.cancel();
		}
	}
}