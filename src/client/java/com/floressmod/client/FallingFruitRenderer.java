package com.floressmod.client;

import com.floressmod.FloressMod;
import com.floressmod.block.FruitBlock;
import com.floressmod.entity.FallingFruitEntity;
import com.floressmod.registry.ModBlocks;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/** Рисует исходную блоковую модель и разбивает её на исходные части во время land-анимации. */
public class FallingFruitRenderer extends EntityRenderer<FallingFruitEntity, FallingFruitRenderState> {
	public static final Identifier BROKEN_LEFT_MODEL =
			Identifier.of(FloressMod.MOD_ID, "block/fruit_stage2_broken_left");
	public static final Identifier BROKEN_RIGHT_MODEL =
			Identifier.of(FloressMod.MOD_ID, "block/fruit_stage2_broken_right");

	private static final float[] RELEASE_TIMES = {0.0f, 0.0833f, 0.25f, 0.5833f, 0.9583f, 1.25f};
	private static final float[] RELEASE_ROTATION_Z = {0.0f, -5.0f, 6.89f, -6.51f, -5.0f, 0.0f};

	private final BlockRenderManager blockRenderManager;

	public FallingFruitRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.blockRenderManager = context.getBlockRenderManager();
		this.shadowRadius = 0.35f;
	}

	@Override
	public FallingFruitRenderState createRenderState() {
		return new FallingFruitRenderState();
	}

	@Override
	public void updateRenderState(FallingFruitEntity entity, FallingFruitRenderState state, float tickDelta) {
		super.updateRenderState(entity, state, tickDelta);
		state.blockState = entity.getFruitState().orElseGet(() ->
				ModBlocks.FRUIT.getDefaultState().with(FruitBlock.AGE, FruitBlock.RIPE_AGE));
		state.phase = entity.getAnimationPhase();
		state.phaseTicks = entity.getAnimationPhaseTicks() + tickDelta;
	}

	@Override
	public void render(FallingFruitRenderState state, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		switch (state.phase) {
			case FallingFruitEntity.PHASE_RELEASE -> renderRelease(state, matrices, vertexConsumers, light);
			case FallingFruitEntity.PHASE_LAND -> renderLand(state, matrices, vertexConsumers, light);
			default -> renderFall(state, matrices, vertexConsumers, light);
		}
		matrices.pop();
		super.render(state, matrices, vertexConsumers, light);
	}

	private void renderRelease(FallingFruitRenderState state, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		float seconds = Math.min(state.phaseTicks / 20.0f, 1.25f);
		float rotationZ = sample(seconds, RELEASE_TIMES, RELEASE_ROTATION_Z);
		float progress = seconds / 1.25f;
		float drop = -0.08f * progress * progress;
		applyTransform(matrices, 0.0f, 0.55f, 0.0f,
				0.0f, drop, 0.0f, 0.0f, 0.0f, rotationZ, 1.0f, 1.0f, 1.0f);
		renderWhole(state.blockState, matrices, vertexConsumers, light);
	}

	private void renderFall(FallingFruitRenderState state, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		float seconds = (state.phaseTicks / 20.0f) % 1.75f;
		float peak = seconds <= 1.0417f
				? seconds / 1.0417f
				: 1.0f - (seconds - 1.0417f) / (1.75f - 1.0417f);
		peak = smooth(peak);
		float pulse = seconds <= 0.875f
				? seconds / 0.875f
				: 1.0f - (seconds - 0.875f) / 0.875f;
		pulse = smooth(pulse);
		applyTransform(matrices, 0.0f, 0.55f, 0.0f,
				-0.9f / 16.0f, 1.2f / 16.0f, 0.0f,
				-1.6917f * peak, -12.1168f * peak, 2.7209f * peak,
				MathHelper.lerp(pulse, 1.0f, 0.8f),
				MathHelper.lerp(pulse, 1.0f, 1.2f),
				MathHelper.lerp(pulse, 1.0f, 0.9f));
		renderWhole(state.blockState, matrices, vertexConsumers, light);
	}

	private void renderLand(FallingFruitRenderState state, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		float seconds = Math.min(state.phaseTicks / 20.0f, 0.5f);
		float squash;
		if (seconds <= 0.125f) {
			squash = smooth(seconds / 0.125f);
		} else if (seconds <= 0.2083f) {
			squash = 1.0f - smooth((seconds - 0.125f) / (0.2083f - 0.125f));
		} else {
			squash = 0.0f;
		}

		applyTransform(matrices, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
				MathHelper.lerp(squash, 1.0f, 1.8f),
				MathHelper.lerp(squash, 1.0f, 0.7f),
				MathHelper.lerp(squash, 1.0f, 1.7f));

		float breakProgress = seconds <= 0.125f ? 0.0f : smooth((seconds - 0.125f) / 0.375f);
		float pieceScale = seconds <= 0.2083f
				? 1.0f
				: 1.0f - smooth((seconds - 0.2083f) / (0.5f - 0.2083f));

		BakedModel left = getExtraModel(BROKEN_LEFT_MODEL);
		BakedModel right = getExtraModel(BROKEN_RIGHT_MODEL);
		renderPiece(state.blockState, left, matrices, vertexConsumers, light,
				1.125f / 16.0f, 10.875f / 16.0f,
				MathHelper.lerp(breakProgress, 0.0f, 30.0f) / 16.0f,
				MathHelper.lerp(breakProgress, 0.0f, -6.9f) / 16.0f,
				-11.528f * breakProgress, -0.6525f * breakProgress, -131.2144f * breakProgress,
				pieceScale);
		renderPiece(state.blockState, right, matrices, vertexConsumers, light,
				-2.0f / 16.0f, 9.33333f / 16.0f,
				MathHelper.lerp(breakProgress, 0.0f, -23.6f) / 16.0f,
				MathHelper.lerp(breakProgress, 0.0f, -4.8f) / 16.0f,
				-2.9028f * breakProgress, 3.0443f * breakProgress, 136.7315f * breakProgress,
				pieceScale);
	}

	private void renderWhole(BlockState state, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.translate(-0.5, 0.0, -0.5);
		this.blockRenderManager.renderBlockAsEntity(
				state, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
		matrices.pop();
	}

	private void renderPiece(BlockState state, BakedModel model, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light,
			float pivotX, float pivotY, float moveX, float moveY,
			float rotationX, float rotationY, float rotationZ, float scale) {
		matrices.push();
		applyTransform(matrices, pivotX, pivotY, 0.0f,
				moveX, moveY, 0.0f, rotationX, rotationY, rotationZ, scale, scale, scale);
		matrices.translate(-0.5, 0.0, -0.5);
		VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(state));
		this.blockRenderManager.getModelRenderer().render(
				matrices.peek(), vertices, state, model,
				1.0f, 1.0f, 1.0f, light, OverlayTexture.DEFAULT_UV);
		matrices.pop();
	}

	private static BakedModel getExtraModel(Identifier id) {
		return ((FabricBakedModelManager) MinecraftClient.getInstance().getBakedModelManager()).getModel(id);
	}

	private static void applyTransform(MatrixStack matrices,
			float pivotX, float pivotY, float pivotZ,
			float moveX, float moveY, float moveZ,
			float rotationX, float rotationY, float rotationZ,
			float scaleX, float scaleY, float scaleZ) {
		matrices.translate(moveX, moveY, moveZ);
		matrices.translate(pivotX, pivotY, pivotZ);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationZ));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
		matrices.scale(scaleX, scaleY, scaleZ);
		matrices.translate(-pivotX, -pivotY, -pivotZ);
	}

	private static float sample(float time, float[] times, float[] values) {
		for (int i = 1; i < times.length; i++) {
			if (time <= times[i]) {
				float progress = (time - times[i - 1]) / (times[i] - times[i - 1]);
				return MathHelper.lerp(smooth(progress), values[i - 1], values[i]);
			}
		}
		return values[values.length - 1];
	}

	private static float smooth(float value) {
		value = MathHelper.clamp(value, 0.0f, 1.0f);
		return value * value * (3.0f - 2.0f * value);
	}
}
