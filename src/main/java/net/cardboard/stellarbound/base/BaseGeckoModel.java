package net.cardboard.stellarbound.base;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public abstract class BaseGeckoModel<T extends GeoAnimatable> extends GeoModel<T> {

    protected final String modId;
    protected final String name;

    public BaseGeckoModel(String modId, String name) {
        this.modId = modId;
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                modId, "geo/" + name + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                modId, "textures/block/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                modId, "animations/" + name + ".animation.json");
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
