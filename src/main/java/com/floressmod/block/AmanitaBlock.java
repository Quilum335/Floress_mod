package com.floressmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class AmanitaBlock extends MushroomPlantBlock {
	
	public AmanitaBlock(Settings settings) {
		super(TreeConfiguredFeatures.HUGE_RED_MUSHROOM, settings);
	
	}

	
	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM);
	}

	
	
	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM);
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		dropStack(world, pos, new ItemStack(this));
	}
}