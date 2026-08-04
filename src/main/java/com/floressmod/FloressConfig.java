package com.floressmod;

/**
 * Все настраиваемые числа мода — в одном месте.
 * Значения репутации подобраны по ТЗ: сильная потеря / небольшая потеря / рост.
 */
public final class FloressConfig {
	private FloressConfig() {}

	// --- Репутация ---
	public static final int REP_MIN = -100;
	public static final int REP_MAX = 100;
	public static final int REP_DEFAULT = 0;

	/** СИЛЬНАЯ потеря: брёвна, листва, ядовитый плющ, свисающие корни. */
	public static final int REP_HIGH_LOSS = -10;
	/** НЕБОЛЬШАЯ потеря: дёрн, трава, морская трава, грибы. */
	public static final int REP_LOW_LOSS = -3;
	/** Убийство живого гриба. */
	public static final int REP_LOSS_LIVING_MUSHROOM_KILL = -5;

	/** Рост: разрушение мёртвой листвы. */
	public static final int REP_GAIN_BREAK_DEAD_LEAVES = 4;
	/** Рост: костная мука по бревну. */
	public static final int REP_GAIN_BONEMEAL_LOG = 5;
	/** Рост: бутылка воды по бревну. */
	public static final int REP_GAIN_WATER_LOG = 5;
	/** Рост: курица очистила землю от червей. */
	public static final int REP_GAIN_WORMS_CLEANED = 8;

	// --- Плоды ---
	/** Длина майнкрафт-суток в тиках. Плод зреет 5 дней. */
	public static final int FRUIT_DAY_TICKS = 24000;
	/** Шанс появления плода при случайном тике листвы рядом с бревном. */
	public static final float FRUIT_GROW_CHANCE = 0.03f;
	/** Радиус взрыва плода-бомбы. */
	public static final float FRUIT_EXPLOSION_POWER = 2.0f;
	/** Радиус превращения мухоморов в живых грибов после взрыва. */
	public static final int FRUIT_EXPLOSION_AMANITA_RADIUS = 4;

	// --- Мелочи ---
	/** Спавнер пропадает после стольких срабатываний. */
	public static final int SPAWNER_MAX_USES = 5;
	/** Источник воды пропадает после стольких набранных бутылок. */
	public static final int WATER_BOTTLE_MAX_USES = 3;
	/** Радиус, в котором игрок получает репутацию за чистку червей курицей. */
	public static final double WORM_CLEAN_REP_RADIUS = 16.0;
}
