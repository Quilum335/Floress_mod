package com.floressmod.client;

import com.floressmod.FloressMod;
import com.floressmod.entity.FlyEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public class FlyRenderer extends MobEntityRenderer<FlyEntity, LivingEntityRenderState, FlyModel> {
	private static final Identifier TEXTURE = Identifier.of(FloressMod.MOD_ID, "textures/entity/fly.png");

	public FlyRenderer(EntityRendererFactory.Context context) {
		super(context, new FlyModel(context.getPart(ModEntityModelLayers.FLY)), 0.15f);
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
