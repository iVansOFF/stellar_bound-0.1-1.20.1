package net.cardboard.stellarbound.block;

import net.cardboard.stellarbound.base.BaseProcessingBlock;
import net.cardboard.stellarbound.entity.InfuseForgeryBlockEntity;
import net.cardboard.stellarbound.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfuseForgeryBlock extends BaseProcessingBlock {

    public InfuseForgeryBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new InfuseForgeryBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type != ModBlockEntities.INFUSE_FORGERY.get()) return null;

        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof InfuseForgeryBlockEntity infuseForgery) {
                InfuseForgeryBlockEntity.tick(level1, pos, state1, infuseForgery);
            }
        };
    }
}