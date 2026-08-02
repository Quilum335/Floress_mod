package com.floressmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/**
 * Мёртвая листва — не опадает (никогда не исчезает сама).
 * Слом = рост репутации (тег rep_gain_break).
 */
public class DeadLeavesBlock extends LeavesBlock {
	public DeadLeavesBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		// мёртвая листва не опадает
	}
}
