package com.floressmod.item;

import com.floressmod.block.FruitBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

/**
 * Предмет-стадия плода: ставит плод сразу на заданной стадии роста
 * (тип по-прежнему случайный и скрыт). Для съёмок и тестов.
 */
public class FruitStageBlockItem extends BlockItem {
	private final int age;

	public FruitStageBlockItem(Block block, Settings settings, int age) {
		super(block, settings);
		this.age = age;
	}

	@Override
	protected BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = super.getPlacementState(ctx);
		return state == null ? null : state.with(FruitBlock.AGE, this.age);
	}
}
