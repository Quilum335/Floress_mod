package com.floressmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class SoftTurfBlock extends Block {
	public static final MapCodec<SoftTurfBlock> CODEC = createCodec(SoftTurfBlock::new);
	public static final IntProperty WEAR = IntProperty.of("wear", 0, 3);

	public SoftTurfBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(WEAR, 0));
	}

	@Override
	protected MapCodec<? extends Block> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WEAR);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState();
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		tryTrample(world, pos, state, entity);
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		tryTrample(world, pos, state, entity);
		super.onLandedUpon(world, state, pos, entity, fallDistance);
	}

	private static void tryTrample(World world, BlockPos pos, BlockState state, Entity entity) {
		if (world.isClient || !(world instanceof ServerWorld serverWorld)) {
			return;
		}
		if (!(entity instanceof LivingEntity) || entity.bypassesSteppingEffects()) {
			return;
		}
		if (entity.age % 8 != 0) {
			return;
		}
		if (serverWorld.random.nextFloat() >= 0.5F) {
			return;
		}
		trample(serverWorld, pos, state);
	}

	private static void trample(ServerWorld world, BlockPos pos, BlockState state) {
		int wear = state.get(WEAR);
		world.playSound(null, pos, SoundEvents.BLOCK_GRASS_HIT, SoundCategory.BLOCKS, 0.6F, 0.8F);

		if (wear >= 3) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState(), Block.NOTIFY_ALL);
			return;
		}

		world.setBlockState(pos, state.with(WEAR, wear + 1), Block.NOTIFY_ALL);
	}
}