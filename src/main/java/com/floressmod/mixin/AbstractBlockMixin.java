package com.floressmod.mixin;

import com.floressmod.block.MushroomBounce;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
	@Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
	private void floress$mushroomCollision(
			BlockState state,
			BlockView world,
			BlockPos pos,
			ShapeContext context,
			CallbackInfoReturnable<VoxelShape> cir
	) {
		if (MushroomBounce.isBounceBlock(state)) {
			cir.setReturnValue(MushroomBounce.shapeFor(state));
		}
	}
}



