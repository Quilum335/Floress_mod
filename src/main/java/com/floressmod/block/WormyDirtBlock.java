package com.floressmod.block;

import net.minecraft.block.Block;

/**
 * Земля с червями. Если на ней стоит курица — она её клюёт
 * (см. PeckWormyDirtGoal), и через пару минут блок становится обычной землёй.
 */
public class WormyDirtBlock extends Block {
	public WormyDirtBlock(Settings settings) {
		super(settings);
	}
}
