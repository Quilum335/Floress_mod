package com.floressmod.mechanics;

import com.floressmod.FloressConfig;
import com.floressmod.reputation.FloressState;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/**
 * Источник воды пропадает, если набрать из него 3 бутылки.
 * Счётчик хранится в сохраняемых данных мира (FloressState).
 */
public final class WaterBottleDepletion {
	private WaterBottleDepletion() {}

	public static void register() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getStackInHand(hand);
			if (!stack.isOf(Items.GLASS_BOTTLE)) {
				return TypedActionResult.PASS;
			}
			BlockHitResult hit = raycastFluid(world, player);
			if (hit.getType() != HitResult.Type.BLOCK) {
				return TypedActionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			FluidState fluid = world.getFluidState(pos);
			if (!fluid.isIn(FluidTags.WATER) || !fluid.isStill()) {
				return TypedActionResult.PASS;
			}
			if (!world.isClient && world instanceof ServerWorld serverWorld) {
				FloressState state = FloressState.get(serverWorld);
				int uses = state.getWaterUses(pos.asLong()) + 1;
				if (uses >= FloressConfig.WATER_BOTTLE_MAX_USES) {
					state.setWaterUses(pos.asLong(), 0);
					// источник убираем в конце тика — иначе ваниль не успеет наполнить 3-ю бутылку
					PendingBlockRemovals.queue(serverWorld, pos);
				} else {
					state.setWaterUses(pos.asLong(), uses);
				}
			}
			// PASS — ваниль наполняет бутылку как обычно
			return TypedActionResult.PASS;
		});
	}

	/** Свой рейкаст по жидкости (ванильный Item#raycast защищён пакетом). */
	private static BlockHitResult raycastFluid(net.minecraft.world.World world, net.minecraft.entity.player.PlayerEntity player) {
		Vec3d eye = player.getCameraPosVec(1.0f);
		Vec3d look = player.getRotationVec(1.0f);
		double reach = player.getBlockInteractionRange();
		Vec3d end = eye.add(look.x * reach, look.y * reach, look.z * reach);
		return world.raycast(new RaycastContext(eye, end,
				RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, player));
	}
}
