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
 * Живой гриб: шляпка, ножка и две ноги с походкой.
 * UV-карта 64x64 под текстуру entity/living_mushroom.png.
 */
public class LivingMushroomModel extends EntityModel<LivingEntityRenderState> {
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public LivingMushroomModel(ModelPart root) {
		super(root);
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild("cap",
				ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, -2.0f, -5.0f, 10.0f, 4.0f, 10.0f),
				ModelTransform.pivot(0.0f, 9.0f, 0.0f));
		root.addChild("stem",
				ModelPartBuilder.create().uv(0, 16).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 4.0f, 5.0f),
				ModelTransform.pivot(0.0f, 9.0f, 0.0f));
		root.addChild("left_leg",
				ModelPartBuilder.create().uv(40, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
				ModelTransform.pivot(-1.5f, 13.0f, 0.0f));
		root.addChild("right_leg",
				ModelPartBuilder.create().uv(40, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
				ModelTransform.pivot(1.5f, 13.0f, 0.0f));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		super.setAngles(state);
		float swing = state.limbAmplitudeMultiplier;
		this.leftLeg.pitch = MathHelper.cos(state.limbFrequency * 0.6662f) * 1.4f * swing;
		this.rightLeg.pitch = MathHelper.cos(state.limbFrequency * 0.6662f + (float) Math.PI) * 1.4f * swing;
	}
}
