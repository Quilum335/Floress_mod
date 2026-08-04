package com.floressmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Живой гриб — агрессивный моб. Дерётся, бегает, ходит.
 * После смерти дропает мухомор (лут-таблица entities/living_mushroom).
 * Модель и анимации (idle/walk/run/hit) — GeckoLib, авторские из gribg.bbmodel.
 */
public class LivingMushroomEntity extends HostileEntity implements GeoEntity {
	/** Клиентская копия наличия цели: getTarget() на клиенте всегда null, поэтому бег заводим через трекер. */
	private static final TrackedData<Boolean> HAS_TARGET =
			DataTracker.registerData(LivingMushroomEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public LivingMushroomEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(HAS_TARGET, false);
	}

	@Override
	public void setTarget(LivingEntity target) {
		super.setTarget(target);
		this.dataTracker.set(HAS_TARGET, target != null);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		// Один контроллер не даёт анимациям движения и удара одновременно перезаписывать одни и те же кости.
		controllers.add(new AnimationController<>(this, "main", 2, this::animationPredicate));
	}

	private PlayState animationPredicate(AnimationState<LivingMushroomEntity> state) {
		if (this.handSwinging) {
			return state.setAndContinue(RawAnimation.begin().thenPlay("hit"));
		}
		if (state.isMoving()) {
			// бег — при погоне за целью, ходьба — при блуждании
			return state.setAndContinue(RawAnimation.begin()
					.thenLoop(this.dataTracker.get(HAS_TARGET) ? "run" : "walk"));
		}
		return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	public static DefaultAttributeContainer.Builder createLivingMushroomAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.MAX_HEALTH, 12.0)
				.add(EntityAttributes.ATTACK_DAMAGE, 3.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.FOLLOW_RANGE, 16.0)
				.add(EntityAttributes.ARMOR, 1.0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, false));
		this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		this.goalSelector.add(4, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
}
