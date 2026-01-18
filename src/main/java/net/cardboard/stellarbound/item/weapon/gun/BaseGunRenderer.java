package net.cardboard.stellarbound.item.weapon.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public abstract class BaseGunRenderer<T extends BaseGunItem> extends GeoItemRenderer<T> {

    public BaseGunRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
                             PoseStack poseStack, MultiBufferSource bufferSource,
                             int packedLight, int packedOverlay) {
        // Renderizar normalmente - ya no necesitamos setLastRenderedStack
        // porque la animación se maneja por el estado del ItemStack actual
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}