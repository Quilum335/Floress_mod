package com.floressmod.client;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/**
 * ВРЕМЕННАЯ модель-заглушка мухи (тельце + крылья).
 * Будет заменена финальной моделью от автора.
 */
public class FlyModel extends EntityModel<LivingEntityRenderState> {
	public FlyModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild("body",
				ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 3.0f, 5.0f),
				ModelTransform.pivot(0.0f, 21.0f, 0.0f));
		root.addChild("left_wing",
				ModelPartBuilder.create().uv(0, 8).cuboid(-4.0f, 0.0f, -1.0f, 2.0f, 0.0f, 3.0f),
				ModelTransform.pivot(-1.0f, 21.0f, 0.0f));
		root.addChild("right_wing",
				ModelPartBuilder.create().uv(10, 8).cuboid(2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 3.0f),
				ModelTransform.pivot(1.0f, 21.0f, 0.0f));
		return TexturedModelData.of(modelData, 32, 32);
	}
}
