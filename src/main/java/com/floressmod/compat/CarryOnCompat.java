package com.floressmod.compat;

import com.floressmod.FloressMod;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;

public final class CarryOnCompat {
	private static final Identifier BEFORE_CARRY_ON = FloressMod.id("before_carry_on");

	private CarryOnCompat() {
	}

	public static void register() {
		UseBlockCallback.EVENT.addPhaseOrdering(BEFORE_CARRY_ON, Event.DEFAULT_PHASE);
		UseBlockCallback.EVENT.register(BEFORE_CARRY_ON, (player, world, hand, hitResult) -> {
			if (world.isClient) {
				return ActionResult.PASS;
			}

			var pos = hitResult.getBlockPos();
			if (!world.getBlockState(pos).isOf(Blocks.FLOWER_POT)) {
				return ActionResult.PASS;
			}

			CarryOnData carryData = CarryOnDataManager.getCarryData(player);
			if (!carryData.isCarrying(CarryOnData.CarryType.BLOCK)) {
				return ActionResult.PASS;
			}
			if (!carryData.getBlock().isOf(Blocks.RED_MUSHROOM)) {
				return ActionResult.PASS;
			}

			world.setBlockState(pos, Blocks.POTTED_RED_MUSHROOM.getDefaultState(), 3);
			world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			world.playSound(null, pos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			player.incrementStat(Stats.POT_FLOWER);
			carryData.clear();
			CarryOnDataManager.setCarryData(player, carryData);
			if (player instanceof ServerPlayerEntity serverPlayer) {
				serverPlayer.currentScreenHandler.sendContentUpdates();
			}
			return ActionResult.SUCCESS;
		});
	}
}