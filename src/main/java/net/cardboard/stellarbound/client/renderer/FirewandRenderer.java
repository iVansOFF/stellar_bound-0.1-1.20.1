package net.cardboard.stellarbound.client.renderer;

import net.cardboard.stellarbound.item.FirewandItem;
import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FirewandRenderer extends GeoItemRenderer<FirewandItem> {
    public FirewandRenderer() {
        super(new FirewandModel());
    }

    private static class FirewandModel extends GeoModel<FirewandItem> {
        @Override
        public ResourceLocation getModelResource(FirewandItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(
                    Stellarbound.MOD_ID, "geo/item/firewand.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(FirewandItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(
                    Stellarbound.MOD_ID, "textures/item/item/firewand.png");
        }

        @Override
        public ResourceLocation getAnimationResource(FirewandItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(
                    Stellarbound.MOD_ID, "animations/item/firewand.animation.json");
        }
    }
}