package net.cardboard.stellarbound.item.weapon.gun.client;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.item.weapon.gun.BaseGunModel;
import net.cardboard.stellarbound.item.weapon.gun.FlintlockItem;
import net.minecraft.resources.ResourceLocation;

public class FlintlockModel extends BaseGunModel<FlintlockItem> {

    public FlintlockModel() {
        super("flintlock");
        System.out.println("FlintlockModel inicializado");
    }

    @Override
    public ResourceLocation getModelResource(FlintlockItem animatable) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "geo/weapon/flintlock.geo.json"
        );
        System.out.println("Cargando modelo desde: " + location);
        return location;
    }

    @Override
    public ResourceLocation getTextureResource(FlintlockItem animatable) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "textures/item/weapon/flintlock.png"
        );
        System.out.println("Cargando textura desde: " + location);
        return location;
    }

    @Override
    public ResourceLocation getAnimationResource(FlintlockItem animatable) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                Stellarbound.MOD_ID,
                "animations/weapon/flintlock.animation.json"
        );
        System.out.println("Cargando animaciones desde: " + location);
        return location;
    }
}