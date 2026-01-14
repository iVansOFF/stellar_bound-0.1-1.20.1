package net.cardboard.stellarbound.client.renderer;

import net.cardboard.stellarbound.entity.BulletEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BulletRenderer extends EntityRenderer<BulletEntity> {

    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BulletEntity entity) {
        return null; // No texture needed
    }

    @Override
    public void render(BulletEntity entity, float entityYaw, float partialTicks,
                       com.mojang.blaze3d.vertex.PoseStack poseStack,
                       net.minecraft.client.renderer.MultiBufferSource buffer,
                       int packedLight) {
        // Do nothing - bullet is invisible
    }
}