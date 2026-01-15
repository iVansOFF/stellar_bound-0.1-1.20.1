package net.cardboard.stellarbound.item.weapon.gun.client;

import net.cardboard.stellarbound.item.weapon.gun.BaseGunRenderer;
import net.cardboard.stellarbound.item.weapon.gun.FlintlockItem;

public class FlintlockRenderer extends BaseGunRenderer<FlintlockItem> {

    public FlintlockRenderer() {
        super(new FlintlockModel());
    }

    // BaseGunRenderer ya maneja la actualización del ItemStack para animaciones
}