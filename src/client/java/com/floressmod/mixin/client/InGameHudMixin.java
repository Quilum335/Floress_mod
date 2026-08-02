package com.floressmod.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Убираем ванильную полоску опыта и число уровня —
 * их место занимает шкала репутации дерева (ReputationHud).
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	private void floress$hideExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
	private void floress$hideExperienceLevel(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
		ci.cancel();
	}
}
