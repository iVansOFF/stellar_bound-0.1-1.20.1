package net.cardboard.stellarbound.client.renderer;

import net.cardboard.stellarbound.client.model.InfuseForgeryItemModel;
import net.cardboard.stellarbound.item.InfuseForgeryItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class InfuseForgeryItemRenderer
        extends GeoItemRenderer<InfuseForgeryItem> {

    public InfuseForgeryItemRenderer() {
        super(new InfuseForgeryItemModel());
    }
}