package com.floressmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

public final class MushroomBounce {
	private static final int COMBO_TIMEOUT_TICKS = 30;
	private static final int IDLE_RESET_TICKS = 5;
	private static final int MIN_JUMP_GAP_TICKS = 8;
	private static final int BOUNCE_IMMUNITY_TICKS = 10;
	private static final double BOUNCE_DAMPING = 0.55;

	public static final VoxelShape RED_MUSHROOM_OUTLINE = VoxelShapes.union(
			Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 3.0, 9.0),
			Block.createCuboidShape(4.0, 3.0, 4.0, 12.0, 5.0, 12.0),
			Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 6.0, 11.0),
			Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 7.0, 10.0)
	);

	public static final VoxelShape RED_MUSHROOM_COLLISION = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 6.0, 12.0);

	public static final VoxelShape POTTED_RED_MUSHROOM_SHAPE = VoxelShapes.union(
			Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0),
			Block.createCuboidShape(7.0, 4.0, 7.0, 9.0, 7.0, 9.0),
			Block.createCuboidShape(4.0, 7.0, 4.0, 12.0, 9.0, 12.0),
			Block.createCuboidShape(5.0, 9.0, 5.0, 11.0, 10.0, 11.0),
			Block.createCuboidShape(6.0, 10.0, 6.0, 10.0, 11.0, 10.0)
	);

	private static final Map<Entity, State> STATES = new WeakHashMap<>();

	
	private MushroomBounce() {
	}

	public static boolean isBounceBlock(BlockState state) {
		if (state.isOf(Blocks.RED_MUSHROOM) || state.isOf(Blocks.POTTED_RED_MUSHROOM)) {
			return true;
		}
		return state.getBlock() instanceof FlowerPotBlock pot && pot.getContent() == Blocks.RED_MUSHROOM;
	}

	
	
	public static boolean isBounceBlock(Block block) {
		return block == Blocks.RED_MUSHROOM || block == Blocks.POTTED_RED_MUSHROOM;
	}

	public static VoxelShape shapeFor(BlockState state) {
		if (state.isOf(Blocks.RED_MUSHROOM)) {
			return RED_MUSHROOM_COLLISION;
		}
		if (isBounceBlock(state)) {
			return POTTED_RED_MUSHROOM_SHAPE;
		}
		return VoxelShapes.empty();
	}

	
	public static boolean isStandingOnBounceBlock(Entity entity) {
		World world = entity.getWorld();
		BlockPos feet = BlockPos.ofFloored(entity.getX(), entity.getY() - 0.05, entity.getZ());
		return isBounceBlock(world.getBlockState(feet)) || isBounceBlock(world.getBlockState(feet.down()));
	}

	public static void clear(Entity entity) {
		STATES.remove(entity);
	}

	public static void onLandedUpon(World world, Entity entity, float fallDistance) {
		if (entity.bypassesLandingEffects()) {
			return;
		}
		entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
	}

	public static boolean trySlimeBounce(Entity entity) {
		State state = STATES.get(entity);
		if (state != null) {
			if (state.skipNextBounce) {
				state.skipNextBounce = false;
				state.combo = 0;
				state.baseY = 0.0;
				state.idleTicks = 0;
				return false;
			}
			if (entity.age < state.bounceImmuneUntil) {
				return false;
			}
		}

		Vec3d velocity = entity.getVelocity();
		if (velocity.y >= 0.0) {
			return false;
		}

		entity.setVelocity(velocity.x, -velocity.y * BOUNCE_DAMPING, velocity.z);
		entity.velocityModified = true;
		return true;
	}

	public static void onSteppedOn(Entity entity) {
		double absY = Math.abs(entity.getVelocity().y);
		if (absY >= 0.1 || entity.bypassesSteppingEffects()) {
			return;
		}
		double factor = 0.4 + absY * 0.2;
		entity.setVelocity(entity.getVelocity().multiply(factor, 1.0, factor));
	}

	public static void tick(Entity entity) {
		State state = STATES.get(entity);
		if (state == null) {
			return;
		}

		if (!isStandingOnBounceBlock(entity)) {
			if (entity.isOnGround()) {
				STATES.remove(entity);
			}
			return;
		}

		if (!entity.isOnGround()) {
			state.idleTicks = 0;
			return;
		}

		state.idleTicks++;
		if (state.idleTicks >= IDLE_RESET_TICKS) {
			STATES.remove(entity);
		}
	}
    // Егор, этот часть кода написал чатджпт с грибом
	public static void onJump(LivingEntity entity, float vanillaJumpY) {
		if (entity.bypassesSteppingEffects() || !isStandingOnBounceBlock(entity)) {
			STATES.remove(entity);
			return;
		}

		State state = STATES.computeIfAbsent(entity, ignored -> new State());
		state.bounceImmuneUntil = entity.age + BOUNCE_IMMUNITY_TICKS;
		state.idleTicks = 0;

		Vec3d velocity = entity.getVelocity();

		if (entity.age - state.lastJumpAge < MIN_JUMP_GAP_TICKS) {
			entity.setVelocity(velocity.x, vanillaJumpY, velocity.z);
			entity.velocityModified = true;
			return;
		}

		if (entity.age - state.lastJumpAge > COMBO_TIMEOUT_TICKS) {
			state.combo = 0;
			state.baseY = 0.0;
			state.skipNextBounce = false;
		}

		state.lastJumpAge = entity.age;

		if (state.combo <= 0) {
			state.combo = 1;
			state.baseY = vanillaJumpY;
			entity.setVelocity(velocity.x, vanillaJumpY, velocity.z);
			entity.velocityModified = true;
			return;
		}

		if (state.combo == 1) {
			state.combo = 2;
			entity.setVelocity(velocity.x, state.baseY * Math.sqrt(2.0), velocity.z);
			entity.velocityModified = true;
			return;
		}

		entity.setVelocity(velocity.x, state.baseY * Math.sqrt(3.0), velocity.z);
		entity.velocityModified = true;
		state.combo = 0;
		state.baseY = 0.0;
		state.skipNextBounce = true;
	}

	private static final class State {
		private int combo;
		private double baseY;
		private int lastJumpAge = Integer.MIN_VALUE / 2;
		private int idleTicks;
		private boolean skipNextBounce;
		private int bounceImmuneUntil;
	}
}