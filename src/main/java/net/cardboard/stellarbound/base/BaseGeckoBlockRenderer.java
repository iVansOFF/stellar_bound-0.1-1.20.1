package net.cardboard.stellarbound.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public abstract class BaseGeckoBlockRenderer<
        T extends BlockEntity & GeoBlockEntity>
        extends GeoBlockRenderer<T> {

    protected final DirectionProperty facingProperty;

    public BaseGeckoBlockRenderer(GeoModel<T> model, DirectionProperty facingProperty) {
        super(model);
        this.facingProperty = facingProperty;
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        float rotation = switch (facing) {
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> 270f;
            default -> 0f;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
    }
}
