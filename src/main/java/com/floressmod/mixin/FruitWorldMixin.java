package com.floressmod.mixin;

import com.floressmod.block.FruitBlock;
import com.floressmod.registry.ModBlocks;
import com.floressmod.world.FruitFallTicker;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Ловит в том числе смену age=4 на age=5 у уже стоящего плода через команду/debug stick. */
@Mixin(World.class)
public abstract class FruitWorldMixin {
	@Inject(
			method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
			at = @At("RETURN")
	)
	private void floress$scheduleAgeFiveFruit(BlockPos pos, BlockState state, int flags, int maxUpdateDepth,
			CallbackInfoReturnable<Boolean> cir) {
		if (!Boolean.TRUE.equals(cir.getReturnValue())) {
			return;
		}
		if ((Object) this instanceof ServerWorld serverWorld
				&& state.isOf(ModBlocks.FRUIT)
				&& state.get(FruitBlock.AGE) == FruitBlock.RIPE_AGE + 1) {
			FruitFallTicker.schedule(serverWorld, pos);
		}
	}
}
