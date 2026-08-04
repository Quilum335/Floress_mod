package com.floressmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
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
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public LivingMushroomEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate));
		controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate));
	}

	private PlayState movementPredicate(AnimationState<LivingMushroomEntity> state) {
		if (state.isMoving()) {
			// бег — при погоне за целью, ходьба — при блуждании
			return state.setAndContinue(RawAnimation.begin()
					.thenLoop(this.getTarget() != null ? "run" : "walk"));
		}
		return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
	}

	private PlayState attackPredicate(AnimationState<LivingMushroomEntity> state) {
		if (this.handSwinging) {
			if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
				state.getController().forceAnimationReset();
				return state.setAndContinue(RawAnimation.begin().thenPlay("hit"));
			}
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
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
