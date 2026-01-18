package net.cardboard.stellarbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cardboard.stellarbound.client.model.SkraeveModel;
import net.cardboard.stellarbound.entity.SkraeveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SkraeveRenderer extends GeoEntityRenderer<SkraeveEntity> {

    // Textura de glow para los ojos
    private static final ResourceLocation GLOW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("stellarbound", "textures/entity/skraeve_glow.png");

    public SkraeveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkraeveModel());
        this.shadowRadius = 1.5f;

        // Añadir capa personalizada de glow
        this.addRenderLayer(new EyesGlowLayer(this));
    }

    @Override
    public void render(
            @NotNull SkraeveEntity entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();
        poseStack.scale(1.5f, 1.5f, 1.5f);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    // Capa para ojos brillantes
    public static class EyesGlowLayer extends GeoRenderLayer<SkraeveEntity> {
        public EyesGlowLayer(GeoEntityRenderer<SkraeveEntity> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack poseStack, SkraeveEntity animatable,
                           BakedGeoModel bakedModel, RenderType renderType,
                           MultiBufferSource bufferSource, VertexConsumer buffer,
                           float partialTick, int packedLight, int packedOverlay) {

            // Crear VertexConsumer para el efecto de glow
            // RenderType.eyes() siempre se renderiza brillante
            VertexConsumer glowBuffer = bufferSource.getBuffer(RenderType.eyes(GLOW_TEXTURE));

            // Renderizar el modelo completo con la textura de glow
            // Como el fondo es transparente, solo los ojos se verán
            getRenderer().reRender(
                    bakedModel,
                    poseStack,
                    bufferSource,
                    animatable,
                    RenderType.eyes(GLOW_TEXTURE),
                    glowBuffer,
                    partialTick,
                    15728880, // luz máxima
                    packedOverlay,
                    1.0f, 1.0f, 1.0f, 1.0f
            );
        }
    }
}