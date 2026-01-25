package net.cardboard.stellarbound.client.model;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.FireballEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireballModel extends GeoModel<FireballEntity> {

    @Override
    public ResourceLocation getModelResource(FireballEntity entity) {
        return Stellarbound.id("geo/fireball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireballEntity entity) {
        return Stellarbound.id("textures/entity/fireball.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireballEntity entity) {
        return Stellarbound.id("animations/fireball.animation.json");
    }
}