package com.floressmod.client;

import com.floressmod.FloressMod;
import com.floressmod.entity.FlyEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FlyRenderer extends MobEntityRenderer<FlyEntity, FlyRenderState, FlyModel> {
	private static final Identifier TEXTURE = Identifier.of(FloressMod.MOD_ID, "textures/entity/fly.png");

	public FlyRenderer(EntityRendererFactory.Context context) {
		super(context, new FlyModel(context.getPart(ModEntityModelLayers.FLY)), 0.15f);
	}

	@Override
	public FlyRenderState createRenderState() {
		return new FlyRenderState();
	}

	@Override
	public void updateRenderState(FlyEntity entity, FlyRenderState state, float tickDelta) {
		super.updateRenderState(entity, state, tickDelta);
		state.onGround = entity.isOnGround();
	}

	@Override
	public Identifier getTexture(FlyRenderState state) {
		return TEXTURE;
	}
}
