package net.cardboard.stellarbound.client.model;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.item.InfuseForgeryItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InfuseForgeryItemModel extends GeoModel<InfuseForgeryItem> {

    @Override
    public ResourceLocation getModelResource(InfuseForgeryItem animatable) {
        return Stellarbound.id("geo/infuse_forgery.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(InfuseForgeryItem animatable) {
        return Stellarbound.id("textures/block/infuse_forgery.png");
    }

    @Override
    public ResourceLocation getAnimationResource(InfuseForgeryItem animatable) {
        return Stellarbound.id("animations/infuse_forgery.animation.json");
    }

    @Override
    public RenderType getRenderType(InfuseForgeryItem animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}