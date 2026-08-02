package com.floressmod.mixin;

import com.floressmod.FloressConfig;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Спавнер, сработавший 5 раз, пропадает.
 * Срабатывание ловим по сбросу задержки спавна: после успешного спавна
 * spawnDelay снова становится большим (>= minSpawnDelay).
 */
@Mixin(MobSpawnerLogic.class)
public abstract class MobSpawnerLogicMixin {
	@Shadow
	private int spawnDelay;

	@Unique
	private int floress$uses = 0;

	@Unique
	private int floress$delayBeforeTick;

	@Inject(method = "serverTick", at = @At("HEAD"))
	private void floress$captureDelay(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		this.floress$delayBeforeTick = this.spawnDelay;
	}

	@Inject(method = "serverTick", at = @At("TAIL"))
	private void floress$countSpawn(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		// задержка была на нуле, а после тика снова большая — значит, спавн произошёл
		if (this.floress$delayBeforeTick <= 1 && this.spawnDelay > 1) {
			this.floress$uses++;
			if (this.floress$uses >= FloressConfig.SPAWNER_MAX_USES) {
				world.breakBlock(pos, false);
			}
		}
	}
}
