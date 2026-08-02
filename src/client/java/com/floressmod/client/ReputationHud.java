package com.floressmod.client;

import com.floressmod.FloressMod;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/**
 * Шкала репутации дерева — текстура hud_rep.png на месте полоски опыта
 * (сама полоска опыта и число уровня скрыты InGameHudMixin; сам опыт
 * и его получение остаются ванильными).
 * Текстура 184x50: верхняя половина (v 0-24) — фон с маской над полосой,
 * нижняя (v 25-49) — заполнение. Полоса внутри виджета на y 16-24.
 * Шкала -100..100, изначально посередине: рост заполняет вправо, падение — влево.
 */
public final class ReputationHud {
	private static final Identifier BAR_TEXTURE = Identifier.of(FloressMod.MOD_ID, "textures/gui/hud_rep.png");

	private static final int BAR_WIDTH = 184;
	private static final int WIDGET_HEIGHT = 25;
	private static final int TEXTURE_HEIGHT = 50;
	private static final int BAR_TOP_IN_WIDGET = 16;
	private static final int HALF = BAR_WIDTH / 2;

	private ReputationHud() {}

	public static void register() {
		HudRenderCallback.EVENT.register(ReputationHud::render);
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.interactionManager == null) {
			return;
		}
		if (!client.interactionManager.hasStatusBars()) {
			return; // креатив/наблюдатель — без шкалы
		}
		int reputation = ClientReputation.get();

		int centerX = context.getScaledWindowWidth() / 2;
		int x = centerX - HALF;
		int y = context.getScaledWindowHeight() - 33 - BAR_TOP_IN_WIDGET; // полоса — на месте полоски опыта

		context.drawTexture(RenderLayer::getGuiTextured, BAR_TEXTURE,
				x, y, 0, 0, BAR_WIDTH, WIDGET_HEIGHT, BAR_WIDTH, TEXTURE_HEIGHT);

		int amount = (int) (Math.abs(reputation) / 100.0 * HALF);
		if (reputation > 0) {
			context.drawTexture(RenderLayer::getGuiTextured, BAR_TEXTURE,
					centerX, y, HALF, 25, amount, WIDGET_HEIGHT, BAR_WIDTH, TEXTURE_HEIGHT);
		} else if (reputation < 0) {
			context.drawTexture(RenderLayer::getGuiTextured, BAR_TEXTURE,
					centerX - amount, y, HALF - amount, 25, amount, WIDGET_HEIGHT, BAR_WIDTH, TEXTURE_HEIGHT);
		}

		String text = String.valueOf(reputation);
		int color = reputation >= 0 ? 0xA0FFA0 : 0xFFA0A0;
		context.drawTextWithShadow(client.textRenderer, text, centerX + 11, y + 9, color);
	}
}
