package net.cardboard.stellarbound.item.weapon.gun;

import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public abstract class BaseGunModel<T extends BaseGunItem> extends GeoModel<T> {

    private final String gunName;

    public BaseGunModel(String gunName) {
        this.gunName = gunName;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID,
                "geo/weapon/" + gunName + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID,
                "textures/item/weapon/" + gunName + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID,
                "animations/weapon/" + gunName + ".animation.json");
    }
}