package com.floressmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public final class PluschBlock extends PlantBlock {
	public static final MapCodec<PluschBlock> CODEC = createCodec(PluschBlock::new);

	private static final VoxelShape SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);

	public PluschBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends PlantBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.slowMovement(state, new Vec3d(0.8, 0.75D, 0.8));
		if (!(world instanceof net.minecraft.server.world.ServerWorld serverWorld)
				|| !(entity instanceof LivingEntity living)) {
			return;
		}
		if (living.age % 10 != 0) {
			return;
		}
		living.damage(serverWorld, world.getDamageSources().sweetBerryBush(), 2.0F);
		living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
	}
}