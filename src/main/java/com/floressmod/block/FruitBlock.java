package com.floressmod.block;

import com.floressmod.FloressConfig;
import com.floressmod.entity.FallingFruitEntity;
import com.floressmod.fruit.FruitLandEffects;
import com.floressmod.fruit.FruitType;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/**
 * Плод дерева. Висит под листвой, зреет 5 майнкрафт-суток
 * (5 стадий по 1 дню через планируемые тики — время сохраняется
 * при выгрузке чанка), затем с небольшим случайным разбросом падает.
 */
public class FruitBlock extends Block {
	public static final MapCodec<FruitBlock> CODEC = createCodec(FruitBlock::new);

	public static final EnumProperty<FruitType> TYPE = EnumProperty.of("type", FruitType.class);
	/** 0..3 — растёт, 4 — созрел, 5 — созрел и ждёт случайной задержки падения. */
	public static final IntProperty AGE = IntProperty.of("age", 0, 5);
	public static final int RIPE_AGE = 4;

	private static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 8.0, 5.0, 11.0, 16.0, 11.0);

	public FruitBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState()
				.with(TYPE, FruitType.HARVEST)
				.with(AGE, 0));
	}

	@Override
	protected MapCodec<? extends Block> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TYPE, AGE);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		// плод висит ПОД листвой
		return world.getBlockState(pos.up()).isIn(BlockTags.LEAVES);
	}

	@Override
	public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
		// из предмета ставится сразу спелый плод случайного типа
		return this.getDefaultState()
				.with(TYPE, FruitType.random(ctx.getWorld().getRandom()))
				.with(AGE, RIPE_AGE);
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		if (!world.isClient) {
			if (state.get(AGE) < RIPE_AGE) {
				world.scheduleBlockTick(pos, this, FloressConfig.FRUIT_DAY_TICKS);
			} else {
				world.scheduleBlockTick(pos, this, 20 + world.random.nextInt(181));
			}
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int age = state.get(AGE);
		if (age < RIPE_AGE) {
			// прошёл ещё один день роста
			world.setBlockState(pos, state.with(AGE, age + 1));
			world.scheduleBlockTick(pos, this, FloressConfig.FRUIT_DAY_TICKS);
		} else if (age == RIPE_AGE) {
			// созрел: случайный разброс в несколько секунд, чтобы плоды не падали разом
			world.setBlockState(pos, state.with(AGE, RIPE_AGE + 1));
			world.scheduleBlockTick(pos, this, 20 + random.nextInt(181)); // 1–10 секунд
		} else {
			// время падать
			if (world.getBlockState(pos.down()).isAir()) {
				world.removeBlock(pos, false);
				// отрывание: хруст ветки и пара частиц
				world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,
						net.minecraft.sound.SoundCategory.BLOCKS, 0.8f, 0.9f);
				world.spawnParticles(new net.minecraft.particle.BlockStateParticleEffect(
								net.minecraft.particle.ParticleTypes.BLOCK, state),
						pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.25, 0.2, 0.25, 0.02);
				FallingFruitEntity.spawn(world, pos, state.with(AGE, RIPE_AGE));
			} else {
				// падать некуда — срабатывает на месте
				world.removeBlock(pos, false);
				FruitLandEffects.trigger(world, pos, state.get(TYPE));
			}
		}
	}
}
