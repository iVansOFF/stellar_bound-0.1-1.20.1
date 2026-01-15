package net.cardboard.stellarbound.item.weapon.gun;

import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public abstract class BaseGunRenderer<T extends BaseGunItem> extends GeoItemRenderer<T> {

    public BaseGunRenderer(GeoModel<T> model) {
        super(model);
    }

    // No necesitamos sobrescribir getCurrentItemStack() aquí
}