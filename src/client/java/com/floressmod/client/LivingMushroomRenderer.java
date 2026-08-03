package com.floressmod.client;

import com.floressmod.entity.LivingMushroomEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LivingMushroomRenderer extends GeoEntityRenderer<LivingMushroomEntity> {
	public LivingMushroomRenderer(EntityRendererFactory.Context context) {
		super(context, new LivingMushroomGeoModel());
	}
}
