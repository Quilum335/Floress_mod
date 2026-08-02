package com.floressmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Муха — маленький летающий агрессивный моб из плода-мухи.
 * Летает без гравитации, кружит и пикирует на игрока.
 * TODO: по ТЗ это мухи из Alex's Mobs, которого нет на 1.21.4 — своя замена.
 */
public class FlyEntity extends HostileEntity {
	public FlyEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlyMoveControl(this);
		this.setNoGravity(true);
	}

	public static DefaultAttributeContainer.Builder createFlyAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.MAX_HEALTH, 6.0)
				.add(EntityAttributes.ATTACK_DAMAGE, 2.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.35)
				.add(EntityAttributes.FOLLOW_RANGE, 24.0);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new FlySwoopAttackGoal(this));
		this.goalSelector.add(2, new FlyWanderGoal(this));
		this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}

	/** Свободное перемещение в 3D: летит к целевой точке напрямую. */
	private static class FlyMoveControl extends MoveControl {
		FlyMoveControl(MobEntity entity) {
			super(entity);
		}

		@Override
		public void tick() {
			if (this.state != MoveControl.State.MOVE_TO) {
				return;
			}
			Vec3d diff = new Vec3d(
					this.targetX - this.entity.getX(),
					this.targetY - this.entity.getY(),
					this.targetZ - this.entity.getZ());
			double length = diff.length();
			if (length < 0.4) {
				this.state = MoveControl.State.WAIT;
				this.entity.setVelocity(this.entity.getVelocity().multiply(0.4));
				return;
			}
			double slowdown = Math.min(1.0, length * 0.5);
			Vec3d velocity = diff.normalize().multiply(this.speed * slowdown);
			this.entity.setVelocity(velocity);
			float yaw = (float) (Math.atan2(velocity.z, velocity.x) * 180.0 / Math.PI) - 90.0f;
			this.entity.setYaw(yaw);
			this.entity.bodyYaw = yaw;
			this.entity.headYaw = yaw;
		}
	}

	/** Пикирует на цель и кусает в упор. */
	private static class FlySwoopAttackGoal extends Goal {
		private final FlyEntity fly;
		private int cooldown;

		FlySwoopAttackGoal(FlyEntity fly) {
			this.fly = fly;
			this.setControls(EnumSet.of(Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return this.fly.getTarget() != null && this.fly.getTarget().isAlive();
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart();
		}

		@Override
		public void tick() {
			var target = this.fly.getTarget();
			if (target == null) {
				return;
			}
			if (--this.cooldown <= 0) {
				this.fly.getMoveControl().moveTo(
						target.getX(),
						target.getY() + target.getStandingEyeHeight() * 0.5,
						target.getZ(),
						1.4);
				this.cooldown = 12 + this.fly.getRandom().nextInt(12);
			}
			if (this.fly.squaredDistanceTo(target) < 2.0 && this.fly.getWorld() instanceof ServerWorld serverWorld) {
				this.fly.tryAttack(serverWorld, target);
			}
		}
	}

	/** Бесцельно кружит рядом с текущей точкой. */
	private static class FlyWanderGoal extends Goal {
		private final FlyEntity fly;
		private int cooldown = 20;

		FlyWanderGoal(FlyEntity fly) {
			this.fly = fly;
			this.setControls(EnumSet.of(Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return this.fly.getTarget() == null;
		}

		@Override
		public void tick() {
			if (--this.cooldown > 0) {
				return;
			}
			this.cooldown = 40 + this.fly.getRandom().nextInt(60);
			double x = this.fly.getX() + this.fly.getRandom().nextInt(13) - 6;
			double y = this.fly.getY() + this.fly.getRandom().nextInt(7) - 2;
			double z = this.fly.getZ() + this.fly.getRandom().nextInt(13) - 6;
			if (this.fly.getWorld().isAir(net.minecraft.util.math.BlockPos.ofFloored(x, y, z))) {
				this.fly.getMoveControl().moveTo(x, y, z, 0.7);
			}
		}
	}
}
