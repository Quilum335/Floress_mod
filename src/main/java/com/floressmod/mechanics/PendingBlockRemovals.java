package com.floressmod.mechanics;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Очередь блоков, которые надо убрать в конце текущего тика.
 * Используется, когда блок нельзя удалить прямо в обработчике
 * (например, ваниль ещё должна дочитать его в этом же клике).
 */
public final class PendingBlockRemovals {
	private static final List<Map.Entry<ServerWorld, BlockPos>> QUEUE = new ArrayList<>();

	private PendingBlockRemovals() {}

	public static void queue(ServerWorld world, BlockPos pos) {
		QUEUE.add(Map.entry(world, pos.toImmutable()));
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (QUEUE.isEmpty()) {
				return;
			}
			for (Map.Entry<ServerWorld, BlockPos> entry : QUEUE) {
				entry.getKey().setBlockState(entry.getValue(), Blocks.AIR.getDefaultState());
			}
			QUEUE.clear();
		});
	}
}
