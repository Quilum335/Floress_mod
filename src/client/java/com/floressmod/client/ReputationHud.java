package com.floressmod.client;

import com.floressmod.FloressMod;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/**
 * Шкала репутации дерева — рисуется на месте полоски опыта
 * (сама полоска опыта скрыта InGameHudMixin).
 * -100..100, изначально посередине (0). Рост — зелёное вправо,
 * падение — красное влево.
 */
public final class ReputationHud {
	private static final int BAR_WIDTH = 182;
	private static final int BAR_HEIGHT = 5;

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

		int x = context.getScaledWindowWidth() / 2 - BAR_WIDTH / 2;
		int y = context.getScaledWindowHeight() - 32 + 3; // ровно там, где была полоска опыта

		// фон
		context.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF101010);
		context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFF3A2E20);

		int center = x + BAR_WIDTH / 2;
		int amount = (int) (Math.abs(reputation) / 100.0 * (BAR_WIDTH / 2 - 2));
		if (reputation > 0) {
			context.fill(center, y, center + amount, y + BAR_HEIGHT, 0xFF4CAF50);
		} else if (reputation < 0) {
			context.fill(center - amount, y, center, y + BAR_HEIGHT, 0xFFB23A2E);
		}
		// центральная метка
		context.fill(center, y - 1, center + 1, y + BAR_HEIGHT + 1, 0xFFFFFFFF);

		String text = String.valueOf(reputation);
		int color = reputation >= 0 ? 0xA0FFA0 : 0xFFA0A0;
		context.drawCenteredTextWithShadow(client.textRenderer, text, center, y - 9, color);
	}
}
