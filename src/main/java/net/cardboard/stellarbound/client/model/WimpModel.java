package net.cardboard.stellarbound.client.model;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.WimpEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WimpModel extends GeoModel<WimpEntity> {

    @Override
    public ResourceLocation getModelResource(WimpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "geo/wimp.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(WimpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "textures/entity/wimp.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(WimpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "animations/wimp.animation.json"
        );
    }
}