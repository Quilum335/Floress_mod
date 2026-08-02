package com.floressmod.mixin;

import com.floressmod.block.ModBlocks;
import com.floressmod.entity.ai.PeckWormyDirtGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity {
	protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void floress$addPeckGoal(CallbackInfo ci) {
		this.goalSelector.add(2, new PeckWormyDirtGoal((ChickenEntity) (Object) this));
	}

	@Inject(method = "tickMovement", at = @At("TAIL"))
	private void floress$flapWhilePecking(CallbackInfo ci) {
		ChickenEntity self = (ChickenEntity) (Object) this;
		if (self.getPitch() <= 40.0F) {
			return;
		}
		BlockPos feet = BlockPos.ofFloored(self.getX(), self.getY() - 0.05, self.getZ());
		World world = self.getWorld();
		if (!world.getBlockState(feet).isOf(ModBlocks.WORMY_DIRT)
				&& !world.getBlockState(feet.down()).isOf(ModBlocks.WORMY_DIRT)) {
			return;
		}
		self.flapSpeed = 2.0F;
		self.maxWingDeviation = 1.0F;
		self.flapProgress += 0.8F;
	}
}