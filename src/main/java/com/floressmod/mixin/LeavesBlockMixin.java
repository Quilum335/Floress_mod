package com.floressmod.mixin;

import com.floressmod.fruit.FruitGrowthLogic;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Плоды растут из случайных тиков листвы рядом со стволом.
 * Ваниль даёт случайные тики только опадающей листве (distance 7),
 * поэтому включаем их для всей листвы — опадение при этом
 * по-прежнему регулируется внутри randomTick ванили.
 */
@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin {
	@Inject(method = "hasRandomTicks", at = @At("HEAD"), cancellable = true)
	private void floress$alwaysRandomTick(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@Inject(method = "randomTick", at = @At("TAIL"))
	private void floress$growFruit(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		FruitGrowthLogic.tryGrow(world, pos, state, random);
	}
}
