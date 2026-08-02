package com.floressmod.registry;

import com.floressmod.FloressMod;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * Блочные теги, через которые настраивается влияние на репутацию.
 * Любые блоки (в т.ч. из других модов) можно добавить в эти теги датапаком.
 */
public final class ModTags {
	private ModTags() {}

	/** Слом = СИЛЬНАЯ потеря репутации (брёвна, листва, плющ, свисающие корни). */
	public static final TagKey<Block> REP_HIGH_LOSS = tag("rep_high_loss");
	/** Слом = НЕБОЛЬШАЯ потеря репутации (дёрн, трава, морская трава, грибы). */
	public static final TagKey<Block> REP_LOW_LOSS = tag("rep_low_loss");
	/** Слом = РОСТ репутации (мёртвая листва). */
	public static final TagKey<Block> REP_GAIN_BREAK = tag("rep_gain_break");
	/** Природные блоки, которые взрыв плода-бомбы НЕ разрушает. */
	public static final TagKey<Block> EXPLOSION_NATURAL = tag("explosion_natural");

	private static TagKey<Block> tag(String name) {
		return TagKey.of(RegistryKeys.BLOCK, Identifier.of(FloressMod.MOD_ID, name));
	}
}
