package com.floressmod.entity.ai;

import com.floressmod.registry.ModBlocks;
import com.floressmod.block.entity.WormyDirtBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public final class PeckWormyDirtGoal extends Goal {
	private static final int SEARCH_RANGE = 10;
	private static final double MOUNT_DISTANCE_SQ = 2.25;
	private static final double GIVE_UP_DISTANCE_SQ = 36.0;
	private static final int MAX_AWAY_TICKS = 80;

	private final ChickenEntity chicken;
	private BlockPos target;
	private int repathCooldown;
	private int awayTicks;
	private boolean mounted;

	public PeckWormyDirtGoal(ChickenEntity chicken) {
		this.chicken = chicken;
		this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
	}

	@Override
	public boolean canStart() {
		if (chicken.isBaby() || chicken.isInLove()) {
			return false;
		}
		target = findWormyDirt();
		return target != null;
	}

	@Override
	public boolean shouldContinue() {
		if (target == null || chicken.isBaby()) {
			return false;
		}
		World world = chicken.getWorld();
		if (!world.getBlockState(target).isOf(ModBlocks.WORMY_DIRT)) {
			return false;
		}
		if (!world.getBlockState(target.up()).isAir()) {
			return false;
		}
		BlockEntity blockEntity = world.getBlockEntity(target);
		if (!(blockEntity instanceof WormyDirtBlockEntity wormyDirt) || !wormyDirt.isFreeOrClaimedBy(chicken)) {
			return false;
		}
		double dist = chicken.squaredDistanceTo(target.getX() + 0.5, target.getY() + 1.0, target.getZ() + 0.5);
		return dist <= GIVE_UP_DISTANCE_SQ || awayTicks < MAX_AWAY_TICKS;
	}

	@Override
	public void start() {
		repathCooldown = 0;
		awayTicks = 0;
		mounted = false;
		moveToTarget();
	}

	@Override
	public void stop() {
		releaseClaim();
		target = null;
		awayTicks = 0;
		mounted = false;
		chicken.getNavigation().stop();
	}

	@Override
	public void tick() {
		if (target == null) {
			return;
		}

		World world = chicken.getWorld();
		if (!world.getBlockState(target).isOf(ModBlocks.WORMY_DIRT) || !world.getBlockState(target.up()).isAir()) {
			target = null;
			return;
		}

		BlockEntity blockEntity = world.getBlockEntity(target);
		if (!(blockEntity instanceof WormyDirtBlockEntity wormyDirt) || !wormyDirt.isFreeOrClaimedBy(chicken)) {
			target = null;
			return;
		}

		Vec3d standPos = new Vec3d(target.getX() + 0.5, target.getY() + 1.0, target.getZ() + 0.5);
		boolean onTop = chicken.getBlockPos().down().equals(target);

		if (!onTop) {
			mounted = false;
			awayTicks++;
			double distSq = chicken.squaredDistanceTo(standPos);

			if (distSq <= MOUNT_DISTANCE_SQ && chicken.isOnGround()) {
				chicken.getNavigation().stop();
				chicken.refreshPositionAndAngles(standPos.x, standPos.y, standPos.z, chicken.getYaw(), 0.0F);
				chicken.setVelocity(Vec3d.ZERO);
				chicken.velocityModified = true;
				mounted = true;
				return;
			}

			if (--repathCooldown <= 0) {
				repathCooldown = 10;
				moveToTarget();
			}
			chicken.getLookControl().lookAt(standPos.x, standPos.y, standPos.z);
			return;
		}

		awayTicks = 0;
		chicken.getNavigation().stop();

		if (!mounted) {
			chicken.refreshPositionAndAngles(standPos.x, standPos.y, standPos.z, chicken.getYaw(), chicken.getPitch());
			mounted = true;
		} else {
			double dx = standPos.x - chicken.getX();
			double dz = standPos.z - chicken.getZ();
			if (dx * dx + dz * dz > 0.04) {
				chicken.setPosition(standPos.x, standPos.y, standPos.z);
			}
		}

		chicken.setVelocity(Vec3d.ZERO);
		chicken.velocityModified = true;

		float bob = (float) Math.sin(chicken.age * 0.85) * 16.0F;
		chicken.getLookControl().lookAt(standPos.x, target.getY() + 0.05, standPos.z);
		chicken.setPitch(58.0F + bob);

		if (!world.isClient) {
			wormyDirt.peck(world, target, chicken);
		}
	}

	private void releaseClaim() {
		if (target == null || chicken.getWorld().isClient) {
			return;
		}
		BlockEntity blockEntity = chicken.getWorld().getBlockEntity(target);
		if (blockEntity instanceof WormyDirtBlockEntity wormyDirt) {
			wormyDirt.releaseClaim(chicken);
		}
	}

	private void moveToTarget() {
		chicken.getNavigation().startMovingTo(
				target.getX() + 0.5,
				target.getY() + 1.0,
				target.getZ() + 0.5,
				1.2
		);
	}

	private BlockPos findWormyDirt() {
		BlockPos origin = chicken.getBlockPos();
		BlockPos best = null;
		double bestDist = Double.MAX_VALUE;
		World world = chicken.getWorld();

		for (BlockPos pos : BlockPos.iterate(
				origin.add(-SEARCH_RANGE, -2, -SEARCH_RANGE),
				origin.add(SEARCH_RANGE, 2, SEARCH_RANGE))) {
			if (!world.getBlockState(pos).isOf(ModBlocks.WORMY_DIRT)) {
				continue;
			}
			if (!world.getBlockState(pos.up()).isAir()) {
				continue;
			}
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (!(blockEntity instanceof WormyDirtBlockEntity wormyDirt) || !wormyDirt.isFreeOrClaimedBy(chicken)) {
				continue;
			}

			double dist = chicken.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
			if (dist < bestDist) {
				bestDist = dist;
				best = pos.toImmutable();
			}
		}

		return best;
	}
}