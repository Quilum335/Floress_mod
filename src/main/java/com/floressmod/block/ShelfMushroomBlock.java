package com.floressmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/**
 * Трутовик (shelf mushroom из ванильной 26.3, порт на 1.21.4):
 * крепится к боковым граням блоков (4 направления), растёт в двух стадиях
 * (age 0 — маленький, age 1 — большой), костная мука выращивает маленький,
 * большой падает двумя предметами, при приземлении на него — подбрасывает.
 * Геометрия форм повторяет ванильные модели shelf_mushroom_stage0/1.
 */
public class ShelfMushroomBlock extends Block implements net.minecraft.block.Fertilizable {
	public static final MapCodec<ShelfMushroomBlock> CODEC = createCodec(ShelfMushroomBlock::new);

	/** Направление, куда «смотрит» гриб (прочь от опоры). Только горизонталь, как в ванили. */
	public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class,
			Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
	/** 0 — маленький, 1 — большой. */
	public static final IntProperty AGE = IntProperty.of("age", 0, 1);

	/** Геометрия ванильной модели stage0 (facing=north, опора на z=16). */
	private static final double[][] SMALL_CUBOIDS = {
			{3, 9, 9, 13, 11, 16},
			{5, 8, 12, 11, 9, 16}
	};
	/** Геометрия ванильной модели stage1 (facing=north, опора на z=16). */
	private static final double[][] LARGE_CUBOIDS = {
			{1, 8, 6, 15, 11, 16},
			{4, 6, 10, 12, 8, 16}
	};

	public ShelfMushroomBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState()
				.with(FACING, Direction.NORTH).with(AGE, 0));
	}

	@Override
	protected MapCodec<? extends Block> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, AGE);
	}

	private static VoxelShape shapeFor(Direction facing, int age) {
		VoxelShape shape = VoxelShapes.empty();
		for (double[] b : age == 1 ? LARGE_CUBOIDS : SMALL_CUBOIDS) {
			shape = VoxelShapes.union(shape, switch (facing) {
				case NORTH -> Block.createCuboidShape(b[0], b[1], b[2], b[3], b[4], b[5]);
				case SOUTH -> Block.createCuboidShape(b[0], b[1], 16 - b[5], b[3], b[4], 16 - b[2]);
				case EAST -> Block.createCuboidShape(16 - b[5], b[1], b[0], 16 - b[2], b[4], b[3]);
				case WEST -> Block.createCuboidShape(b[2], b[1], b[0], b[5], b[4], b[3]);
				default -> VoxelShapes.fullCube();
			});
		}
		return shape;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return shapeFor(state.get(FACING), state.get(AGE));
	}

	@Override
	public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
		// как в ванили: ставится только на боковые грани, смотрит наружу от опоры
		Direction side = ctx.getSide();
		if (!side.getAxis().isHorizontal()) {
			return null;
		}
		return this.getDefaultState().with(FACING, side);
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction support = state.get(FACING).getOpposite();
		BlockPos supportPos = pos.offset(support);
		return world.getBlockState(supportPos).isSideSolidFullSquare(world, supportPos, support.getOpposite());
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world,
												 net.minecraft.world.tick.ScheduledTickView tickView, BlockPos pos,
												 Direction direction, BlockPos neighborPos, BlockState neighborState,
												 Random random) {
		if (direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		}
		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return state.get(AGE) == 0;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(AGE, 1), Block.NOTIFY_ALL);
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		// упругость трутовика: половина урона от падения
		if (entity.bypassesLandingEffects()) {
			super.onLandedUpon(world, state, pos, entity, fallDistance);
		} else {
			super.onLandedUpon(world, state, pos, entity, fallDistance * 0.5f);
		}
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		if (entity.bypassesLandingEffects()) {
			super.onEntityLand(world, entity);
			return;
		}
		if (entity.getVelocity().y < 0.0) {
			// подброс на 75% скорости падения, как у кровати
			entity.setVelocity(entity.getVelocity().x, -entity.getVelocity().y * 0.75, entity.getVelocity().z);
			entity.velocityModified = true;
			if (entity.getWorld() instanceof ServerWorld serverWorld) {
				serverWorld.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_SLIME_BLOCK_STEP,
						SoundCategory.BLOCKS, 0.8f, 0.9f);
			}
		}
	}
}
