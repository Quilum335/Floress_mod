package com.floressmod.entity.goal;

import com.floressmod.FloressConfig;
import com.floressmod.registry.ModBlocks;
import com.floressmod.reputation.ReputationManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Курица, стоящая на земле с червями, клюёт её.
 * Через 1–3 минуты земля становится обычной, а ближайший игрок
 * получает репутацию («очищение земли от червей»).
 */
public class PeckWormyDirtGoal extends Goal {
	private final ChickenEntity chicken;
	private int peckTicks;
	private int requiredTicks;

	public PeckWormyDirtGoal(ChickenEntity chicken) {
		this.chicken = chicken;
		this.setControls(EnumSet.of(Control.MOVE));
	}

	@Override
	public boolean canStart() {
		return this.chicken.isOnGround() && this.isOnWormyDirt();
	}

	@Override
	public boolean shouldContinue() {
		return this.chicken.isOnGround() && this.isOnWormyDirt();
	}

	private boolean isOnWormyDirt() {
		return this.chicken.getWorld().getBlockState(this.chicken.getBlockPos().down()).isOf(ModBlocks.WORMY_DIRT);
	}

	@Override
	public void start() {
		this.chicken.getNavigation().stop();
		this.peckTicks = 0;
		// «пара минут» с разбросом: 60–180 секунд
		this.requiredTicks = 1200 + this.chicken.getRandom().nextInt(2401);
	}

	@Override
	public void tick() {
		this.peckTicks++;
		World world = this.chicken.getWorld();
		if (this.peckTicks % 20 == 0) {
			world.playSound(null, this.chicken.getBlockPos(), SoundEvents.ENTITY_CHICKEN_AMBIENT,
					SoundCategory.NEUTRAL, 0.7f, 0.8f + this.chicken.getRandom().nextFloat() * 0.4f);
			if (world instanceof ServerWorld serverWorld) {
				BlockState soil = ModBlocks.WORMY_DIRT.getDefaultState();
				serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, soil),
						this.chicken.getX(), this.chicken.getY() + 0.1, this.chicken.getZ(),
						6, 0.25, 0.1, 0.25, 0.02);
			}
		}
		if (this.peckTicks >= this.requiredTicks && world instanceof ServerWorld serverWorld) {
			BlockPos soilPos = this.chicken.getBlockPos().down();
			serverWorld.setBlockState(soilPos, Blocks.DIRT.getDefaultState());
			serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
					soilPos.getX() + 0.5, soilPos.getY() + 1.0, soilPos.getZ() + 0.5,
					8, 0.4, 0.4, 0.4, 0.05);
			// репутация растёт у игрока, хотя чистят куры, а не он
			ServerPlayerEntity nearest = null;
			double nearestDistance = FloressConfig.WORM_CLEAN_REP_RADIUS;
			for (ServerPlayerEntity candidate : net.fabricmc.fabric.api.networking.v1.PlayerLookup.around(
					serverWorld, this.chicken.getPos(), FloressConfig.WORM_CLEAN_REP_RADIUS)) {
				double distance = candidate.squaredDistanceTo(this.chicken);
				if (distance <= nearestDistance * nearestDistance) {
					nearest = candidate;
					nearestDistance = Math.sqrt(distance);
				}
			}
			if (nearest != null) {
				ReputationManager.add(nearest, FloressConfig.REP_GAIN_WORMS_CLEANED);
			}
			this.stop();
		}
	}
}
