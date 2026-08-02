package com.floressmod.reputation;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class ReputationNetworking {
	private ReputationNetworking() {}

	public static void registerServer() {
		PayloadTypeRegistry.playS2C().register(ReputationSyncPayload.ID, ReputationSyncPayload.CODEC);
		// при входе игрока сразу отправляем его репутацию
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				ReputationManager.sync(handler.player));
	}
}
