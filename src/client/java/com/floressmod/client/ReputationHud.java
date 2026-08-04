package com.floressmod.client;

import com.floressmod.FloressMod;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public final class ReputationHud {
	private static final Identifier BAR_TEXTURE = Identifier.of(FloressMod.MOD_ID, "textures/gui/hud_rep.png");

	private static final int BAR_WIDTH = 184;
	private static final int WIDGET_HEIGHT = 25;
	private static final int TEXTURE_HEIGHT = 50;
	private static final int BAR_TOP_IN_WIDGET = 16;
	private static final int HALF = BAR_WIDTH / 2;
	private static final float ANIM_SPEED = 8.0F;

	private static Float displayed = null;

	private ReputationHud() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register(ReputationHud::render);
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.interactionManager == null) {
			displayed = null;
			return;
		}
		if (!client.interactionManager.hasStatusBars()) {
			return;
		}

		float target = ClientReputation.get();
		if (displayed == null) {
			displayed = target;
		} else {
			float dt = tickCounter.getLastFrameDuration() / 20.0F;
			float alpha = 1.0F - (float) Math.exp(-ANIM_SPEED * dt);
			displayed = MathHelper.lerp(alpha, displayed, target);
		}

		int x = context.getScaledWindowWidth() / 2 - HALF;
		int y = context.getScaledWindowHeight() - 33 - BAR_TOP_IN_WIDGET;

		context.drawTexture(RenderLayer::getGuiTextured, BAR_TEXTURE,
				x, y, 0, 0, BAR_WIDTH, WIDGET_HEIGHT, BAR_WIDTH, TEXTURE_HEIGHT);

		int extent = Math.round(Math.abs(displayed) / 100.0F * HALF);
		extent = MathHelper.clamp(extent, 0, HALF);
		if (extent <= 0) {
			return;
		}

		if (displayed > 0.0F) {
			context.drawTexture(RenderLayer::getGuiTextured, BAR_TEXTURE,
					x + HALF, y, HALF, 25, extent, WIDGET_HEIGHT, BAR_WIDTH, TEXTURE_HEIGHT);
		} else {
			context.drawTexture(RenderLayer::getGuiTextured, BAR_TEXTURE,
					x + HALF - extent, y, HALF - extent, 25, extent, WIDGET_HEIGHT, BAR_WIDTH, TEXTURE_HEIGHT);
		}
	}
}
