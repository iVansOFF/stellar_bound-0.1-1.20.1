package net.cardboard.stellarbound.item.weapon.gun.client;

import net.cardboard.stellarbound.item.weapon.gun.BaseGunItem;
import net.cardboard.stellarbound.item.weapon.gun.FlintlockItem;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FlintlockRenderer extends GeoItemRenderer<FlintlockItem> {

    public FlintlockRenderer() {
        super(new FlintlockModel());
    }

    @Override
    public void renderByItem(ItemStack stack, net.minecraft.world.item.ItemDisplayContext transformType,
                             com.mojang.blaze3d.vertex.PoseStack poseStack,
                             net.minecraft.client.renderer.MultiBufferSource bufferSource,
                             int packedLight, int packedOverlay) {
        // Actualizar el último item stack renderizado en BaseGunItem
        BaseGunItem.setLastRenderedStack(stack);

        // Llamar al método padre para renderizar
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}