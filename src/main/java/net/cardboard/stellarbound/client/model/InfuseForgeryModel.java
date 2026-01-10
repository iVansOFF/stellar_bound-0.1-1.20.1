package net.cardboard.stellarbound.client.model;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.InfuseForgeryBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InfuseForgeryModel extends GeoModel<InfuseForgeryBlockEntity> {

    @Override
    public ResourceLocation getModelResource(InfuseForgeryBlockEntity animatable) {
        return Stellarbound.id("geo/infuse_forgery.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(InfuseForgeryBlockEntity animatable) {
        return Stellarbound.id("textures/block/infuse_forgery.png");
    }

    @Override
    public ResourceLocation getAnimationResource(InfuseForgeryBlockEntity animatable) {
        return Stellarbound.id("animations/infuse_forgery.animation.json");
    }
    @Override
    public RenderType getRenderType(InfuseForgeryBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}