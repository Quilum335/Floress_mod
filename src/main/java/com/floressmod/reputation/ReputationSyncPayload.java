package com.floressmod.reputation;

import com.floressmod.FloressMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Пакет S2C: текущее значение репутации игрока. */
public record ReputationSyncPayload(int value) implements CustomPayload {
	public static final CustomPayload.Id<ReputationSyncPayload> ID =
			new CustomPayload.Id<>(Identifier.of(FloressMod.MOD_ID, "reputation_sync"));

	public static final PacketCodec<RegistryByteBuf, ReputationSyncPayload> CODEC =
			PacketCodec.tuple(PacketCodecs.INTEGER, ReputationSyncPayload::value, ReputationSyncPayload::new);

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return ID;
	}
}
