package net.cardboard.stellarbound.client.renderer;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.FireballEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireballRenderer extends GeoEntityRenderer<FireballEntity> {

    public FireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(Stellarbound.id("fireball")));

        // Configuración del renderizado
        this.shadowRadius = 0.25f;
    }

    @Override
    public void render(FireballEntity entity, float entityYaw, float partialTick,
                       com.mojang.blaze3d.vertex.PoseStack poseStack,
                       net.minecraft.client.renderer.MultiBufferSource bufferSource,
                       int packedLight) {
        // Escala opcional si el modelo es muy grande
        float scale = 0.5f;
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}