package net.cardboard.stellarbound.item.weapon.gun;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BaseGunRenderer<T extends BaseGunItem> extends GeoItemRenderer<T> {

    public BaseGunRenderer(GeoModel<T> model) {
        super(model);
    }
}