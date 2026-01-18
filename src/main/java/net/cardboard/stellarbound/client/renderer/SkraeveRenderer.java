package net.cardboard.stellarbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cardboard.stellarbound.client.model.SkraeveModel;
import net.cardboard.stellarbound.entity.SkraeveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkraeveRenderer extends GeoEntityRenderer<SkraeveEntity> {

    public SkraeveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkraeveModel());
        this.shadowRadius = 1.5f;
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
        poseStack.scale(1.5f, 1.5f, 1.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}