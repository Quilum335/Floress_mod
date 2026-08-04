package com.floressmod.block;

import com.floressmod.registry.ModBlocks;
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

	private static final double LIVING_RESTITUTION = 0.8;
	private static final double OTHER_RESTITUTION = 0.65;

	private static final Map<Entity, Boolean> FALL_BOUNCE_LOCK = new WeakHashMap<>();
	private static final Map<Entity, Charge> CHARGES = new WeakHashMap<>();

	private MushroomBounce() {
	}

	public static boolean isBounceBlock(BlockState state) {
		if (state.isOf(Blocks.RED_MUSHROOM) || state.isOf(Blocks.POTTED_RED_MUSHROOM) || state.isOf(ModBlocks.AMANITA)) {
			return true;
		}
		return state.getBlock() instanceof FlowerPotBlock pot && pot.getContent() == Blocks.RED_MUSHROOM;
	}

	public static boolean isBounceBlock(Block block) {
		return block == Blocks.RED_MUSHROOM || block == Blocks.POTTED_RED_MUSHROOM || block == ModBlocks.AMANITA;
	}

	public static VoxelShape shapeFor(BlockState state) {
		if (state.isOf(Blocks.RED_MUSHROOM) || state.isOf(ModBlocks.AMANITA)) {
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

	public static void onLandedUpon(World world, Entity entity, float fallDistance) {
		if (entity.bypassesLandingEffects()) {
			return;
		}
		entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
	}

	public static void applyFallBounce(Entity entity) {
		if (entity.bypassesLandingEffects()) {
			return;
		}

		Vec3d velocity = entity.getVelocity();
		if (velocity.y >= 0.0) {
			return;
		}
		if (Boolean.TRUE.equals(FALL_BOUNCE_LOCK.get(entity))) {
			return;
		}

		double restitution = entity instanceof LivingEntity ? LIVING_RESTITUTION : OTHER_RESTITUTION;
		entity.setVelocity(velocity.x, -velocity.y * restitution, velocity.z);
		entity.velocityModified = true;
		FALL_BOUNCE_LOCK.put(entity, true);
	}

	public static void tick(Entity entity) {
		tickFallBounce(entity);
		tickCharge(entity);
	}

	private static void tickFallBounce(Entity entity) {
		if (!Boolean.TRUE.equals(FALL_BOUNCE_LOCK.get(entity))) {
			return;
		}
		if (!entity.isOnGround()) {
			FALL_BOUNCE_LOCK.put(entity, false);
		}
	}

	private static void tickCharge(Entity entity) {
		Charge charge = CHARGES.get(entity);
		if (charge == null) {
			return;
		}
		if (entity.isOnGround() && !isStandingOnBounceBlock(entity)) {
			CHARGES.remove(entity);
			return;
		}
		if (!entity.isOnGround()) {
			charge.leftGround = true;
			return;
		}
		if (charge.leftGround) {
			charge.leftGround = false;
			charge.ready = true;
		}
	}

	public static void onJump(LivingEntity entity, float vanillaJumpY) {
		if (entity.bypassesSteppingEffects() || !isStandingOnBounceBlock(entity)) {
			CHARGES.remove(entity);
			return;
		}
		if (Boolean.TRUE.equals(FALL_BOUNCE_LOCK.get(entity))) {
			return;
		}

		Charge charge = CHARGES.computeIfAbsent(entity, ignored -> new Charge());
		if (charge.stage > 0 && !charge.ready) {
			return;
		}

		charge.ready = false;
		charge.leftGround = false;

		if (charge.stage == 0) {
			charge.stage = 1;
			charge.baseY = vanillaJumpY;
			return;
		}

		Vec3d velocity = entity.getVelocity();
		if (charge.stage == 1) {
			charge.stage = 2;
			entity.setVelocity(velocity.x, charge.baseY * Math.sqrt(2.0), velocity.z);
			entity.velocityModified = true;
			return;
		}

		entity.setVelocity(velocity.x, charge.baseY * Math.sqrt(3.0), velocity.z);
		entity.velocityModified = true;
		CHARGES.remove(entity);
	}

	private static final class Charge {
		private int stage;
		private double baseY;
		private boolean ready = true;
		private boolean leftGround;
	}
}
