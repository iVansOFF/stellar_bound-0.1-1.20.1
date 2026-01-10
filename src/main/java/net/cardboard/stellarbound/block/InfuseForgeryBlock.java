package net.cardboard.stellarbound.block;

import net.cardboard.stellarbound.base.BaseProcessingBlock;
import net.cardboard.stellarbound.entity.InfuseForgeryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InfuseForgeryBlock extends BaseProcessingBlock {

    public InfuseForgeryBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfuseForgeryBlockEntity(pos, state);
    }
}