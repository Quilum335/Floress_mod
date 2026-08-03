package com.floressmod.client;

import com.floressmod.FloressMod;
import com.floressmod.entity.FallingFruitEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class FallingFruitGeoModel extends GeoModel<FallingFruitEntity> {
	@Override
	public Identifier getModelResource(FallingFruitEntity animatable, GeoRenderer<FallingFruitEntity> renderer) {
		return Identifier.of(FloressMod.MOD_ID, "geo/entity/falling_fruit.geo.json");
	}

	@Override
	public Identifier getTextureResource(FallingFruitEntity animatable, GeoRenderer<FallingFruitEntity> renderer) {
		return Identifier.of(FloressMod.MOD_ID, "textures/entity/plod.png");
	}

	@Override
	public Identifier getAnimationResource(FallingFruitEntity animatable) {
		return Identifier.of(FloressMod.MOD_ID, "animations/falling_fruit.animation.json");
	}
}
