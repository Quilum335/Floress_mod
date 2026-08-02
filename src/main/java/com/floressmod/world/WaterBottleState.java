package com.floressmod.world;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WaterBottleState extends PersistentState {
	private static final String ID = "floress_water_bottles";
	private static final Type<WaterBottleState> TYPE = new Type<>(
			WaterBottleState::new,
			WaterBottleState::fromNbt,
			DataFixTypes.LEVEL
	);

	private final Map<Long, Integer> counts = new HashMap<>();

	public static int recordFill(ServerWorld world, BlockPos pos) {
		WaterBottleState state = get(world);
		long key = pos.asLong();
		int next = state.counts.getOrDefault(key, 0) + 1;
		if (next >= 3) {
			state.counts.remove(key);
		} else {
			state.counts.put(key, next);
		}
		state.markDirty();
		return next;
	}

	public static boolean isDraining(ServerWorld world, BlockPos pos) {
		return get(world).counts.containsKey(pos.asLong());
	}

	public static void clear(ServerWorld world, BlockPos pos) {
		WaterBottleState state = get(world);
		if (state.counts.remove(pos.asLong()) != null) {
			state.markDirty();
		}
	}

	public static Set<Map.Entry<Long, Integer>> entries(ServerWorld world) {
		return Collections.unmodifiableSet(get(world).counts.entrySet());
	}

	private static WaterBottleState get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(TYPE, ID);
	}

	private static WaterBottleState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		WaterBottleState state = new WaterBottleState();
		NbtList list = nbt.getList("Counts", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < list.size(); i++) {
			NbtCompound entry = list.getCompound(i);
			state.counts.put(entry.getLong("Pos"), entry.getInt("Count"));
		}
		return state;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		NbtList list = new NbtList();
		for (Map.Entry<Long, Integer> entry : counts.entrySet()) {
			NbtCompound compound = new NbtCompound();
			compound.putLong("Pos", entry.getKey());
			compound.putInt("Count", entry.getValue());
			list.add(compound);
		}
		nbt.put("Counts", list);
		return nbt;
	}
}