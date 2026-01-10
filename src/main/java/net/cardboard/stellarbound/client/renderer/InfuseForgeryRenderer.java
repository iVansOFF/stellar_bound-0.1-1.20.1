package net.cardboard.stellarbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cardboard.stellarbound.block.InfuseForgeryBlock;
import net.cardboard.stellarbound.client.model.InfuseForgeryModel;
import net.cardboard.stellarbound.entity.InfuseForgeryBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class InfuseForgeryRenderer extends GeoBlockRenderer<InfuseForgeryBlockEntity> {

    public InfuseForgeryRenderer() {
        super(new InfuseForgeryModel());
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        float rotation = switch (facing) {
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> 270f;
            default -> 0f; // NORTH
        };

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
    }
}