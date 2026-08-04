package com.floressmod.client;

import com.floressmod.FloressMod;
import com.floressmod.entity.LivingMushroomEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class LivingMushroomGeoModel extends GeoModel<LivingMushroomEntity> {
	@Override
	public Identifier getModelResource(LivingMushroomEntity animatable, GeoRenderer<LivingMushroomEntity> renderer) {
		return Identifier.of(FloressMod.MOD_ID, "geo/entity/living_mushroom.geo.json");
	}

	@Override
	public Identifier getTextureResource(LivingMushroomEntity animatable, GeoRenderer<LivingMushroomEntity> renderer) {
		return Identifier.of(FloressMod.MOD_ID, "textures/entity/gribg.png");
	}

	@Override
	public Identifier getAnimationResource(LivingMushroomEntity animatable) {
		return Identifier.of(FloressMod.MOD_ID, "animations/living_mushroom.animation.json");
	}

	@Override
	public void setCustomAnimations(LivingMushroomEntity animatable, long instanceId,
			AnimationState<LivingMushroomEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		// У авторских ключей рук есть повороты Y/Z, которые в игре заводят руки внутрь тела.
		// Оставляем движение по X: руки продолжают махать вперёд-назад, не пересекая корпус.
		keepArmOutsideBody("bone2");
		keepArmOutsideBody("bone3");
	}

	private void keepArmOutsideBody(String boneName) {
		getBone(boneName).ifPresent(bone -> {
			bone.setRotY(0.0f);
			bone.setRotZ(0.0f);
		});
	}
}
