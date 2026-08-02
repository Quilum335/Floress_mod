package com.floressmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Ядовитый плющ — лиана, отравляющая при касании.
 * Слом = сильная потеря репутации (тег rep_high_loss).
 */
public class PoisonIvyBlock extends VineBlock {
	public PoisonIvyBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && entity instanceof LivingEntity living && !living.hasStatusEffect(StatusEffects.POISON)) {
			living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0));
		}
	}
}
