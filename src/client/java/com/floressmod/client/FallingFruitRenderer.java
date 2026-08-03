package com.floressmod.client;

import com.floressmod.entity.FallingFruitEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FallingFruitRenderer extends GeoEntityRenderer<FallingFruitEntity> {
	public FallingFruitRenderer(EntityRendererFactory.Context context) {
		super(context, new FallingFruitGeoModel());
	}
}
