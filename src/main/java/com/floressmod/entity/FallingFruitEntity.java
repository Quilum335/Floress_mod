package com.floressmod.entity;

import com.floressmod.block.FruitBlock;
import com.floressmod.fruit.FruitType;
import com.floressmod.fruit.FruitLandEffects;
import com.floressmod.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Созревший плод, падающий с дерева. Приземлившись, запускает
 * эффект своего типа (FruitLandEffects) и исчезает — блоком не становится.
 */
public class FallingFruitEntity extends Entity {
	private static final TrackedData<Optional<BlockState>> FRUIT_STATE =
			DataTracker.registerData(FallingFruitEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);

	private int timeFalling;

	public FallingFruitEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public static void spawn(ServerWorld world, BlockPos pos, BlockState fruitState) {
		FallingFruitEntity entity = new FallingFruitEntity(ModEntities.FALLING_FRUIT, world);
		entity.setFruitState(fruitState);
		entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		world.spawnEntity(entity);
	}

	public Optional<BlockState> getFruitState() {
		return this.dataTracker.get(FRUIT_STATE);
	}

	public void setFruitState(BlockState state) {
		this.dataTracker.set(FRUIT_STATE, Optional.of(state));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(FRUIT_STATE, Optional.empty());
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getFruitState().isEmpty()) {
			if (!this.getWorld().isClient) {
				this.discard();
			}
			return;
		}
		this.timeFalling++;
		this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
		this.move(MovementType.SELF, this.getVelocity());
		if (!this.getWorld().isClient) {
			if (this.isOnGround() || this.timeFalling > 600) {
				BlockPos landingPos = this.getBlockPos();
				this.getFruitState().ifPresent(state -> {
					FruitType type = state.contains(FruitBlock.TYPE) ? state.get(FruitBlock.TYPE) : FruitType.HARVEST;
					FruitLandEffects.trigger((ServerWorld) this.getWorld(), landingPos, type);
				});
				this.discard();
				return;
			}
		}
		this.setVelocity(this.getVelocity().multiply(0.98));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("FruitState")) {
			NbtHelper.toBlockState(this.getWorld().getRegistryManager(), nbt.getCompound("FruitState"))
					.result()
					.ifPresent(this::setFruitState);
		}
		this.timeFalling = nbt.getInt("TimeFalling");
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		this.getFruitState().ifPresent(state -> nbt.put("FruitState", NbtHelper.fromBlockState(state)));
		nbt.putInt("TimeFalling", this.timeFalling);
	}

	@Override
	public boolean doesRenderOnFire() {
		return false;
	}
}
