package net.cardboard.stellarbound.client.renderer;

import org.jetbrains.annotations.NotNull;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cardboard.stellarbound.client.model.WimpModel;
import net.cardboard.stellarbound.entity.WimpEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WimpRenderer extends GeoEntityRenderer<WimpEntity> {

    public WimpRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WimpModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public void render(
            @NotNull WimpEntity entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight
    )
    {
        // El Wimp es chiquito y cobarde
        poseStack.scale(0.8f, 0.8f, 0.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}