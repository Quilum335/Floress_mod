package com.floressmod.block;

import net.minecraft.block.VineBlock;

/**
 * Ядовитый плющ — по сути лиана. Слом = сильная потеря репутации (тег rep_high_loss).
 * Ядовитый эффект при касании — TODO позже, когда будут финальные текстуры/баланс.
 */
public class PoisonIvyBlock extends VineBlock {
	public PoisonIvyBlock(Settings settings) {
		super(settings);
	}
}
