package com.floressmod.mixin;

import com.floressmod.block.MushroomBounce;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerPotBlock.class)
public abstract class FlowerPotBlockMixin {
	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	private void floress$pottedMushroomOutline(
			BlockState state,
			BlockView world,
			BlockPos pos,
			ShapeContext context,
			CallbackInfoReturnable<VoxelShape> cir
	) {
		if (state.isOf(Blocks.POTTED_RED_MUSHROOM)) {
			cir.setReturnValue(MushroomBounce.POTTED_RED_MUSHROOM_SHAPE);
		}
	}

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	private void floress$sneakKeepsPottedMushroom(
			BlockState state,
			World world,
			BlockPos pos,
			PlayerEntity player,
			BlockHitResult hit,
			CallbackInfoReturnable<ActionResult> cir
	) {
		if (state.isOf(Blocks.POTTED_RED_MUSHROOM) && player.isSneaking()) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}
}






