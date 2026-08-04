package com.floressmod.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class FallingFruitRenderState extends EntityRenderState {
	public BlockState blockState = Blocks.AIR.getDefaultState();
	public int phase;
	public float phaseTicks;
}
