package com.floressmod.client;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Муха: тельце и два машущих крыла. UV-карта 32x32 под textures/entity/fly.png.
 */
public class FlyModel extends EntityModel<LivingEntityRenderState> {
	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public FlyModel(ModelPart root) {
		super(root);
		this.leftWing = root.getChild("left_wing");
		this.rightWing = root.getChild("right_wing");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild("body",
				ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, 0.0f, -3.0f, 4.0f, 3.0f, 6.0f),
				ModelTransform.pivot(0.0f, 21.0f, 0.0f));
		root.addChild("left_wing",
				ModelPartBuilder.create().uv(0, 10).cuboid(-4.0f, 0.0f, -2.0f, 3.0f, 0.0f, 4.0f),
				ModelTransform.pivot(-1.0f, 21.0f, 0.0f));
		root.addChild("right_wing",
				ModelPartBuilder.create().uv(12, 10).cuboid(1.0f, 0.0f, -2.0f, 3.0f, 0.0f, 4.0f),
				ModelTransform.pivot(1.0f, 21.0f, 0.0f));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		super.setAngles(state);
		float flap = MathHelper.cos(state.age * 1.9f) * 0.8f;
		this.leftWing.roll = -0.3f - flap;
		this.rightWing.roll = 0.3f + flap;
	}
}
