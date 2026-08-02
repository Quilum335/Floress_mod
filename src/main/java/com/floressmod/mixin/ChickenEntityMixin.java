package com.floressmod.mixin;

import com.floressmod.entity.goal.PeckWormyDirtGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Добавляет курице цель «клевать землю с червями». */
@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity {
	protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void floress$addPeckGoal(CallbackInfo ci) {
		this.goalSelector.add(3, new PeckWormyDirtGoal((ChickenEntity) (Object) this));
	}
}
