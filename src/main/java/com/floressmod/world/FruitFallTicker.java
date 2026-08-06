package com.floressmod.world;

import com.floressmod.block.FruitBlock;
import com.floressmod.registry.ModBlocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/** Независимый от очереди block ticks десятисекундный таймер для плодов age=5. */
public final class FruitFallTicker {
	private static final int HANG_TICKS = 200;
	private static final Map<ServerWorld, Map<Long, Long>> PENDING = new WeakHashMap<>();

	private FruitFallTicker() {}

	public static void register() {
		ServerTickEvents.END_WORLD_TICK.register(FruitFallTicker::tick);
	}

	public static void schedule(ServerWorld world, BlockPos pos) {
		PENDING.computeIfAbsent(world, ignored -> new HashMap<>())
				.put(pos.asLong(), world.getTime() + HANG_TICKS);
	}

	private static void tick(ServerWorld world) {
		Map<Long, Long> timers = PENDING.get(world);
		if (timers == null || timers.isEmpty()) {
			return;
		}

		long now = world.getTime();
		Iterator<Map.Entry<Long, Long>> iterator = timers.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Long, Long> timer = iterator.next();
			if (timer.getValue() > now) {
				continue;
			}

			BlockPos pos = BlockPos.fromLong(timer.getKey());
			if (!world.isChunkLoaded(pos)) {
				continue;
			}

			BlockState state = world.getBlockState(pos);
			if (state.isOf(ModBlocks.FRUIT)
					&& state.get(FruitBlock.AGE) == FruitBlock.RIPE_AGE + 1) {
				FruitBlock.dropNow(world, pos, state);
			}
			iterator.remove();
		}

		if (timers.isEmpty()) {
			PENDING.remove(world);
		}
	}
}
