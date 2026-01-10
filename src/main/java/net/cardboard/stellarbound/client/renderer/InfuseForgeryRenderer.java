package net.cardboard.stellarbound.client.renderer;

import net.cardboard.stellarbound.base.BaseGeckoBlockRenderer;
import net.cardboard.stellarbound.client.model.InfuseForgeryModel;
import net.cardboard.stellarbound.entity.InfuseForgeryBlockEntity;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class InfuseForgeryRenderer extends BaseGeckoBlockRenderer<InfuseForgeryBlockEntity> {

    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public InfuseForgeryRenderer() {
        super(new InfuseForgeryModel(), FACING);
    }
}