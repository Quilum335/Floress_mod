package com.floressmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

/**
 * Просто лежащий кирпич. Плоский блок без коллизии, ломается в кирпич (лут-таблица).
 */
public class LyingBrickBlock extends Block {
	private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 5.0, 14.0, 3.0, 11.0);

	public LyingBrickBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
}
