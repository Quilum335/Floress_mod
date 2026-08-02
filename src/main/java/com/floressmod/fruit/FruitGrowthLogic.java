package com.floressmod.fruit;

import com.floressmod.FloressConfig;
import com.floressmod.block.FruitBlock;
import com.floressmod.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

/**
 * Появление плодов: срабатывает из случайного тика листвы
 * (см. LeavesBlockMixin). Плод появляется под листом, если рядом
 * есть бревно — то есть это настоящее дерево.
 */
public final class FruitGrowthLogic {
	private FruitGrowthLogic() {}

	public static void tryGrow(ServerWorld world, BlockPos leafPos, BlockState leafState, Random random) {
		if (random.nextFloat() >= FloressConfig.FRUIT_GROW_CHANCE) {
			return;
		}
		BlockPos fruitPos = leafPos.down();
		if (!world.getBlockState(fruitPos).isAir()) {
			return;
		}
		// рядом (вплотную к листу) должен быть ствол
		boolean nearLog = false;
		for (Direction direction : Direction.values()) {
			if (world.getBlockState(leafPos.offset(direction)).isIn(BlockTags.LOGS)) {
				nearLog = true;
				break;
			}
		}
		if (!nearLog) {
			return;
		}
		// не плодоносим слишком густо: рядом уже есть плод — выходим
		for (BlockPos check : BlockPos.iterate(fruitPos.add(-1, -1, -1), fruitPos.add(1, 1, 1))) {
			if (world.getBlockState(check).isOf(ModBlocks.FRUIT)) {
				return;
			}
		}
		world.setBlockState(fruitPos, ModBlocks.FRUIT.getDefaultState()
				.with(FruitBlock.TYPE, FruitType.random(random))
				.with(FruitBlock.AGE, 0));
	}
}
