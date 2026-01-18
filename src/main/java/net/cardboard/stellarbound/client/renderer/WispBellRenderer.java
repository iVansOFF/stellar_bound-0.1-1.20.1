package net.cardboard.stellarbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cardboard.stellarbound.client.model.WispBellModel;
import net.cardboard.stellarbound.entity.WispBellEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WispBellRenderer extends GeoEntityRenderer<WispBellEntity> {

    public WispBellRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WispBellModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public void render(
            @NotNull WispBellEntity entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight
    )
    {
        poseStack.scale(0.8f, 0.8f, 0.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}