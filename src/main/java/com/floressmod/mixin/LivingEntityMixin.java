package com.floressmod.mixin;

import com.floressmod.block.MushroomBounce;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	protected abstract float getJumpVelocity();

	@Inject(method = "tick", at = @At("TAIL"))
	private void floress$mushroomChargeTick(CallbackInfo ci) {
		MushroomBounce.tick((LivingEntity) (Object) this);
	}

	@Inject(method = "jump", at = @At("TAIL"))
	private void floress$mushroomJump(CallbackInfo ci) {
		MushroomBounce.onJump((LivingEntity) (Object) this, this.getJumpVelocity());
	}
}