package net.cardboard.stellarbound.client.model;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.WispBellEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WispBellModel extends GeoModel<WispBellEntity> {

    @Override
    public ResourceLocation getModelResource(WispBellEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "geo/wisp_bell.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(WispBellEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "textures/entity/wisp_bell.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(WispBellEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "animations/wisp_bell.animation.json"
        );
    }
}