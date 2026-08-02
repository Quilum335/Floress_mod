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
 * ВРЕМЕННАЯ модель-заглушка живого гриба (ножка + шляпка).
 * Будет заменена финальной моделью от автора.
 */
public class LivingMushroomModel extends EntityModel<LivingEntityRenderState> {
	public LivingMushroomModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild("stem",
				ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				ModelTransform.pivot(0.0f, 18.0f, 0.0f));
		root.addChild("cap",
				ModelPartBuilder.create().uv(0, 10).cuboid(-5.0f, -2.0f, -5.0f, 10.0f, 4.0f, 10.0f),
				ModelTransform.pivot(0.0f, 18.0f, 0.0f));
		return TexturedModelData.of(modelData, 64, 64);
	}
}
