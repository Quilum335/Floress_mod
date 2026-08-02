package com.floressmod.client;

import com.floressmod.entity.FallingFruitEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

/** Рисует падающий плод как обычный блок (как падающий песок). */
public class FallingFruitRenderer extends EntityRenderer<FallingFruitEntity, FallingFruitRenderState> {
	private final BlockRenderManager blockRenderManager;

	public FallingFruitRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.blockRenderManager = context.getBlockRenderManager();
		this.shadowRadius = 0.3f;
	}

	@Override
	public FallingFruitRenderState createRenderState() {
		return new FallingFruitRenderState();
	}

	@Override
	public void updateRenderState(FallingFruitEntity entity, FallingFruitRenderState state, float tickDelta) {
		super.updateRenderState(entity, state, tickDelta);
		state.fruitState = entity.getFruitState().orElse(null);
	}

	@Override
	public void render(FallingFruitRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (state.fruitState == null) {
			return;
		}
		matrices.push();
		matrices.translate(-0.5, 0.0, -0.5);
		this.blockRenderManager.renderBlockAsEntity(state.fruitState, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
		matrices.pop();
		super.render(state, matrices, vertexConsumers, light);
	}
}
