package com.floressmod.fruit;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

/**
 * Виды плодов. Начало у всех одинаковое (зреют 5 дней),
 * различается только то, что происходит при падении.
 */
public enum FruitType implements StringIdentifiable {
	/** Спавнит агрессивных мух (плохие частицы). */
	FLY("fly", false),
	/** Выбрасывает картошку и морковь (хорошие частицы). */
	HARVEST("harvest", true),
	/** Взрывается, ломает блоки КРОМЕ природных, мухоморы -> живые грибы (плохие). */
	EXPLOSIVE("explosive", false),
	/** Спавнит кроликов (хорошие). */
	RABBIT("rabbit", true),
	/** Спавнит зомби в железной броне (не фул сет) и живых грибов (плохие). */
	ZOMBIE("zombie", false),
	/** Спавнит куриц и сажает саженцы случайных деревьев (хорошие). */
	CHICKEN("chicken", true);

	private final String name;
	private final boolean good;

	FruitType(String name, boolean good) {
		this.name = name;
		this.good = good;
	}

	@Override
	public String asString() {
		return this.name;
	}

	/** Хорошие или плохие частицы при падении. */
	public boolean isGood() {
		return this.good;
	}

	public static FruitType random(Random random) {
		FruitType[] values = values();
		return values[random.nextInt(values.length)];
	}
}
