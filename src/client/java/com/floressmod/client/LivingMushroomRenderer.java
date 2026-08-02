package com.floressmod.client;

import com.floressmod.FloressMod;
import com.floressmod.entity.LivingMushroomEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public class LivingMushroomRenderer extends MobEntityRenderer<LivingMushroomEntity, LivingEntityRenderState, LivingMushroomModel> {
	private static final Identifier TEXTURE = Identifier.of(FloressMod.MOD_ID, "textures/entity/living_mushroom.png");

	public LivingMushroomRenderer(EntityRendererFactory.Context context) {
		super(context, new LivingMushroomModel(context.getPart(ModEntityModelLayers.LIVING_MUSHROOM)), 0.35f);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
