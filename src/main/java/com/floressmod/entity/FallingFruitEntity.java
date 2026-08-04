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
 * Созревший плод с тремя фазами: отрыв, падение и приземление.
 * На клиенте всегда рисуется исходной baked-моделью третьей стадии, поэтому форма и UV не меняются.
 */
public class FallingFruitEntity extends Entity {
	public static final int PHASE_RELEASE = 0;
	public static final int PHASE_FALL = 1;
	public static final int PHASE_LAND = 2;

	private static final int RELEASE_TICKS = 25; // длина release-анимации (1.25s)
	private static final int LAND_TICKS = 10; // длина land-анимации (0.5s)

	private static final TrackedData<Optional<BlockState>> FRUIT_STATE =
			DataTracker.registerData(FallingFruitEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
	private static final TrackedData<Integer> PHASE =
			DataTracker.registerData(FallingFruitEntity.class, TrackedDataHandlerRegistry.INTEGER);

	private int phaseTicks;

	public FallingFruitEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public static void spawn(ServerWorld world, BlockPos pos, BlockState fruitState) {
		FallingFruitEntity entity = new FallingFruitEntity(ModEntities.FALLING_FRUIT, world);
		entity.setFruitState(fruitState);
		entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		entity.setNoGravity(true);
		world.spawnEntity(entity);
	}

	public Optional<BlockState> getFruitState() {
		return this.dataTracker.get(FRUIT_STATE);
	}

	public void setFruitState(BlockState state) {
		this.dataTracker.set(FRUIT_STATE, Optional.of(state));
	}

	private int getPhase() {
		return this.dataTracker.get(PHASE);
	}

	public int getAnimationPhase() {
		return this.getPhase();
	}

	public int getAnimationPhaseTicks() {
		return this.phaseTicks;
	}

	private void setPhase(int phase) {
		this.dataTracker.set(PHASE, phase);
		this.phaseTicks = 0;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(FRUIT_STATE, Optional.empty());
		builder.add(PHASE, PHASE_RELEASE);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (PHASE.equals(data)) {
			this.phaseTicks = 0;
		}
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
		this.phaseTicks++;
		switch (this.getPhase()) {
			case PHASE_RELEASE -> tickRelease();
			case PHASE_FALL -> tickFall();
			case PHASE_LAND -> tickLand();
		}
	}

	private void tickRelease() {
		// Коротко сохраняем исходную форму на месте, затем срываемся вниз.
		this.setVelocity(net.minecraft.util.math.Vec3d.ZERO);
		if (!this.getWorld().isClient && this.phaseTicks >= RELEASE_TICKS) {
			this.setNoGravity(false);
			this.setPhase(PHASE_FALL);
		}
	}

	private void tickFall() {
		this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
		this.move(MovementType.SELF, this.getVelocity());
		if (!this.getWorld().isClient) {
			if (this.isOnGround() || this.phaseTicks > 600) {
				this.setNoGravity(true);
				this.setVelocity(net.minecraft.util.math.Vec3d.ZERO);
				this.setPhase(PHASE_LAND);
				BlockPos landingPos = this.getBlockPos();
				this.getFruitState().ifPresent(state -> {
					FruitType type = state.contains(FruitBlock.TYPE) ? state.get(FruitBlock.TYPE) : FruitType.HARVEST;
					FruitLandEffects.trigger((ServerWorld) this.getWorld(), landingPos, type);
				});
				return;
			}
		}
		this.setVelocity(this.getVelocity().multiply(0.98));
	}

	private void tickLand() {
		this.setVelocity(net.minecraft.util.math.Vec3d.ZERO);
		if (!this.getWorld().isClient && this.phaseTicks >= LAND_TICKS) {
			this.discard();
		}
	}

	@Override
	public boolean damage(ServerWorld world, net.minecraft.entity.damage.DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("FruitState")) {
			this.setFruitState(NbtHelper.toBlockState(
					this.getWorld().getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.BLOCK),
					nbt.getCompound("FruitState")));
		}
		this.setPhase(nbt.getInt("Phase"));
		this.phaseTicks = nbt.getInt("PhaseTicks");
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		this.getFruitState().ifPresent(state -> nbt.put("FruitState", NbtHelper.fromBlockState(state)));
		nbt.putInt("Phase", this.getPhase());
		nbt.putInt("PhaseTicks", this.phaseTicks);
	}

	@Override
	public boolean doesRenderOnFire() {
		return false;
	}
}
