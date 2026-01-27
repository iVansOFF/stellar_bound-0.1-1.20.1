package net.cardboard.stellarbound.block;

import net.cardboard.stellarbound.registry.ModBlocks;
import net.cardboard.stellarbound.worldgen.tree.SoulwoodTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SoulSapling extends SaplingBlock {

    // Constructor con solo Properties
    public SoulSapling(Properties properties) {
        super(new SoulwoodTreeGrower(), properties);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 12;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.ASTRAL_GRASS_BLOCK.get());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);

        // Verifica que esté en el bloque correcto
        if (this.mayPlaceOn(blockstate, level, blockpos)) {
            // Verifica que tenga suficiente luz
            return level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos);
        }

        return false;
    }
}