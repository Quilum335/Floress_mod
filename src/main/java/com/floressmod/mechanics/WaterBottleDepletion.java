package com.floressmod.mechanics;

import com.floressmod.FloressConfig;
import com.floressmod.reputation.FloressState;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/**
 * РСЃС‚РѕС‡РЅРёРє РІРѕРґС‹ РїСЂРѕРїР°РґР°РµС‚, РµСЃР»Рё РЅР°Р±СЂР°С‚СЊ РёР· РЅРµРіРѕ 3 Р±СѓС‚С‹Р»РєРё.
 * РЎС‡С‘С‚С‡РёРє С…СЂР°РЅРёС‚СЃСЏ РІ СЃРѕС…СЂР°РЅСЏРµРјС‹С… РґР°РЅРЅС‹С… РјРёСЂР° (FloressState).
 */
public final class WaterBottleDepletion {
	private WaterBottleDepletion() {}

	public static void register() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getStackInHand(hand);
			if (!stack.isOf(Items.GLASS_BOTTLE)) {
				return ActionResult.PASS;
			}
			BlockHitResult hit = raycastFluid(world, player);
			if (hit.getType() != HitResult.Type.BLOCK) {
				return ActionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			FluidState fluid = world.getFluidState(pos);
			if (!fluid.isIn(FluidTags.WATER) || !fluid.isStill()) {
				return ActionResult.PASS;
			}
			if (!world.isClient && world instanceof ServerWorld serverWorld) {
				FloressState state = FloressState.get(serverWorld);
				int uses = state.getWaterUses(pos.asLong()) + 1;
				if (uses >= FloressConfig.WATER_BOTTLE_MAX_USES) {
					state.setWaterUses(pos.asLong(), 0);
					// РёСЃС‚РѕС‡РЅРёРє СѓР±РёСЂР°РµРј РІ РєРѕРЅС†Рµ С‚РёРєР° вЂ” РёРЅР°С‡Рµ РІР°РЅРёР»СЊ РЅРµ СѓСЃРїРµРµС‚ РЅР°РїРѕР»РЅРёС‚СЊ 3-СЋ Р±СѓС‚С‹Р»РєСѓ
					PendingBlockRemovals.queue(serverWorld, pos);
				} else {
					state.setWaterUses(pos.asLong(), uses);
				}
			}
			// PASS вЂ” РІР°РЅРёР»СЊ РЅР°РїРѕР»РЅСЏРµС‚ Р±СѓС‚С‹Р»РєСѓ РєР°Рє РѕР±С‹С‡РЅРѕ
			return ActionResult.PASS;
		});
	}

	/** РЎРІРѕР№ СЂРµР№РєР°СЃС‚ РїРѕ Р¶РёРґРєРѕСЃС‚Рё (РІР°РЅРёР»СЊРЅС‹Р№ Item#raycast Р·Р°С‰РёС‰С‘РЅ РїР°РєРµС‚РѕРј). */
	private static BlockHitResult raycastFluid(net.minecraft.world.World world, net.minecraft.entity.player.PlayerEntity player) {
		Vec3d eye = player.getCameraPosVec(1.0f);
		Vec3d look = player.getRotationVec(1.0f);
		double reach = player.getBlockInteractionRange();
		Vec3d end = eye.add(look.x * reach, look.y * reach, look.z * reach);
		return world.raycast(new RaycastContext(eye, end,
				RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, player));
	}
}

