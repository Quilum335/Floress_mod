package com.floressmod.mixin;

import com.floressmod.block.MushroomBounce;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {
	@Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
	private void floress$mushroomFall(
			World world,
			BlockState state,
			BlockPos pos,
			Entity entity,
			float fallDistance,
			CallbackInfo ci
	) {
		if (!MushroomBounce.isBounceBlock(state)) {
			return;
		}
		MushroomBounce.onLandedUpon(world, entity, fallDistance);
		ci.cancel();
	}

	@Inject(method = "onEntityLand", at = @At("HEAD"), cancellable = true)
	private void floress$mushroomEntityLand(BlockView world, Entity entity, CallbackInfo ci) {
		if (!MushroomBounce.isBounceBlock((Block) (Object) this)) {
			return;
		}
		if (entity.bypassesLandingEffects()) {
			return;
		}
		MushroomBounce.applyFallBounce(entity);
		ci.cancel();
	}
}
