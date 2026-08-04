package com.floressmod.client;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

/**
 * Муха — геометрия и анимация портированы 1-в-1 из Alex's Mobs (ModelFly),
 * текстура — их entity/fly.png 32x32. В воздухе машет крыльями,
 * на земле складывает их (±35°) и перебирает лапками.
 */
public class FlyModel extends EntityModel<FlyRenderState> {
	private final ModelPart legs;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart mouth;

	public FlyModel(ModelPart root) {
		super(root);
		ModelPart body = root.getChild("root").getChild("body");
		this.legs = body.getChild("legs");
		this.leftWing = body.getChild("left_wing");
		this.rightWing = body.getChild("right_wing");
		this.mouth = body.getChild("mouth");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot().addChild("root",
				ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
		ModelPartData body = root.addChild("body",
				ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -2.0f, -3.0f, 4.0f, 4.0f, 6.0f),
				ModelTransform.pivot(0.0f, -3.0f, 0.0f));
		body.addChild("legs",
				ModelPartBuilder.create().uv(0, 11).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 1.0f, 5.0f),
				ModelTransform.pivot(0.0f, 2.0f, -2.0f));
		body.addChild("left_wing",
				ModelPartBuilder.create().uv(12, 11).cuboid(0.0f, 0.0f, -1.0f, 4.0f, 0.0f, 3.0f),
				ModelTransform.pivot(1.0f, -2.0f, -1.0f));
		body.addChild("right_wing",
				ModelPartBuilder.create().uv(12, 11).mirrored().cuboid(-4.0f, 0.0f, -1.0f, 4.0f, 0.0f, 3.0f),
				ModelTransform.pivot(-1.0f, -2.0f, -1.0f));
		body.addChild("mouth",
				ModelPartBuilder.create().uv(15, 16).cuboid(0.0f, 0.0f, -1.0f, 0.0f, 4.0f, 2.0f),
				ModelTransform.pivot(0.0f, 0.0f, -3.0f));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(FlyRenderState state) {
		super.setAngles(state);
		this.leftWing.roll = 0.0f;
		this.rightWing.roll = 0.0f;
		this.legs.pitch = 0.0f;
		this.legs.yaw = 0.0f;
		this.mouth.pitch = 0.0f;
		this.mouth.roll = 0.0f;

		float age = state.age;
		float limbSwing = state.limbFrequency;
		float limbSwingAmount = state.limbAmplitudeMultiplier;

		this.mouth.pitch += MathHelper.cos(age * 0.28f - 1.0f) * 0.016f;

		if (state.onGround) {
			this.leftWing.roll = -35.0f * MathHelper.RADIANS_PER_DEGREE;
			this.rightWing.roll = 35.0f * MathHelper.RADIANS_PER_DEGREE;
			this.legs.yaw += MathHelper.cos(limbSwing * 0.84f + 1.0f) * 0.16f * limbSwingAmount;
		} else {
			this.leftWing.roll += MathHelper.cos(age * 1.82f) * 0.8f;
			this.rightWing.roll += -MathHelper.cos(age * 1.82f) * 0.8f;
			this.legs.pitch += MathHelper.cos(age * 0.28f + 1.0f) * 0.16f;
		}
	}
}
