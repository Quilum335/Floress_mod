package com.floressmod.client;

import com.floressmod.FloressMod;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public final class ModEntityModelLayers {
	public static final EntityModelLayer FLY =
			new EntityModelLayer(Identifier.of(FloressMod.MOD_ID, "fly"), "main");

	private ModEntityModelLayers() {}

	public static void register() {
		EntityModelLayerRegistry.registerModelLayer(FLY, FlyModel::getTexturedModelData);
	}
}
