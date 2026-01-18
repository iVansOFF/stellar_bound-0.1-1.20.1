package net.cardboard.stellarbound.client.model;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.SkraeveEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SkraeveModel extends GeoModel<SkraeveEntity> {

    @Override
    public ResourceLocation getModelResource(SkraeveEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "geo/skraeve.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(SkraeveEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "textures/entity/skraeve.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(SkraeveEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "animations/skraeve.animation.json"
        );
    }
}