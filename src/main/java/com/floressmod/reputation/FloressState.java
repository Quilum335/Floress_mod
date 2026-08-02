package com.floressmod.reputation;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Сохраняемые данные мода: репутация игроков.
 * (Счётчики бутылок воды — отдельно, в world/WaterBottleState.)
 */
public class FloressState extends PersistentState {
	private static final String ID = "floress_mod_data";

	private final Map<UUID, Integer> reputation = new HashMap<>();

	public FloressState() {
		super();
	}

	public static final PersistentState.Type<FloressState> TYPE = new PersistentState.Type<>(
			FloressState::new,
			FloressState::fromNbt,
			null);

	public static FloressState get(ServerWorld world) {
		PersistentStateManager manager = world.getServer().getOverworld().getPersistentStateManager();
		return manager.getOrCreate(TYPE, ID);
	}

	public int getReputation(UUID playerId) {
		return this.reputation.getOrDefault(playerId, com.floressmod.FloressConfig.REP_DEFAULT);
	}

	public void setReputation(UUID playerId, int value) {
		this.reputation.put(playerId, value);
		this.markDirty();
	}

	private static FloressState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		FloressState state = new FloressState();
		NbtCompound rep = nbt.getCompound("Reputation");
		for (String key : rep.getKeys()) {
			state.reputation.put(UUID.fromString(key), rep.getInt(key));
		}
		return state;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		NbtCompound rep = new NbtCompound();
		this.reputation.forEach((uuid, value) -> rep.putInt(uuid.toString(), value));
		nbt.put("Reputation", rep);
		return nbt;
	}
}
