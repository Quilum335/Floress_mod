package com.floressmod.mixin;

import com.floressmod.world.WaterBottleState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GlassBottleItem.class)
public abstract class GlassBottleItemMixin {
	@ModifyArg(
			method = "use",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/GlassBottleItem;raycast(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/RaycastContext$FluidHandling;)Lnet/minecraft/util/hit/BlockHitResult;"
			),
			index = 2
	)
	private RaycastContext.FluidHandling floress$allowLoweredWater(RaycastContext.FluidHandling original) {
		return RaycastContext.FluidHandling.ANY;
	}

	@Inject(
			method = "use",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/math/BlockPos;)V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void floress$consumeWaterSource(
			World world,
			PlayerEntity user,
			Hand hand,
			CallbackInfoReturnable<ActionResult> cir,
			List<?> list,
			ItemStack itemStack,
			BlockHitResult blockHitResult,
			BlockPos blockPos
	) {
		if (world.isClient || !(world instanceof ServerWorld serverWorld)) {
			return;
		}
		if (!world.getBlockState(blockPos).isOf(Blocks.WATER)) {
			return;
		}
		if (!world.getFluidState(blockPos).isStill() && !WaterBottleState.isDraining(serverWorld, blockPos)) {
			return;
		}

		int fills = WaterBottleState.recordFill(serverWorld, blockPos);
		if (fills >= 3) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
			return;
		}

		int level = fills == 1 ? 1 : 3;
		world.setBlockState(
				blockPos,
				Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, level),
				Block.NOTIFY_LISTENERS | Block.FORCE_STATE
		);
	}
}