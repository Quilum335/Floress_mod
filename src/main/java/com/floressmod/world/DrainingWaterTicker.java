package com.floressmod.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Set;

public final class DrainingWaterTicker {
	private DrainingWaterTicker() {
	}

	public static void register() {
		ServerTickEvents.END_WORLD_TICK.register(DrainingWaterTicker::tick);
	}

	private static void tick(ServerWorld world) {
		for (Map.Entry<Long, Integer> entry : Set.copyOf(WaterBottleState.entries(world))) {
			int fills = entry.getValue();
			if (fills < 1 || fills > 2) {
				continue;
			}

			BlockPos pos = BlockPos.fromLong(entry.getKey());
			int level = fills == 1 ? 1 : 3;
			var want = Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, level);
			var have = world.getBlockState(pos);

			if (have.isOf(Blocks.WATER) && have.get(FluidBlock.LEVEL) == level) {
				continue;
			}
			if (!have.isAir() && !have.isOf(Blocks.WATER)) {
				WaterBottleState.clear(world, pos);
				continue;
			}

			world.setBlockState(pos, want, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
		}
	}
}