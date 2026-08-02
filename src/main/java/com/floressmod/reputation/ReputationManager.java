package com.floressmod.reputation;

import com.floressmod.FloressConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

/**
 * Шкала репутации дерева: от -100 до 100, изначально посередине (0).
 * Хранится на сервере (переживает перезаход), синхронизируется клиенту.
 */
public final class ReputationManager {
	private ReputationManager() {}

	public static int get(ServerPlayerEntity player) {
		return FloressState.get((ServerWorld) player.getWorld()).getReputation(player.getUuid());
	}

	public static void set(ServerPlayerEntity player, int value) {
		int clamped = MathHelper.clamp(value, FloressConfig.REP_MIN, FloressConfig.REP_MAX);
		FloressState.get((ServerWorld) player.getWorld()).setReputation(player.getUuid(), clamped);
		sync(player);
	}

	public static void add(ServerPlayerEntity player, int delta) {
		set(player, get(player) + delta);
	}

	public static void sync(ServerPlayerEntity player) {
		if (ServerPlayNetworking.canSend(player, ReputationSyncPayload.ID)) {
			ServerPlayNetworking.send(player, new ReputationSyncPayload(get(player)));
		}
	}
}
