package net.cardboard.stellarbound.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.cardboard.stellarbound.registry.ModBlocks;

public class AstralSoilBlock extends Block {

    public AstralSoilBlock(Properties properties) {
        super(properties);
        // NO registrar eventos aquí
    }

    private void tryConvertFromNearbyGrass(ServerLevel level, BlockPos pos, RandomSource random) {
        // Verificar condiciones de luz
        BlockPos abovePos = pos.above();
        if (level.getMaxLocalRawBrightness(abovePos) >= 9 &&
                !level.getBlockState(abovePos).isSolidRender(level, abovePos)) {

            // Buscar Astral Grass Block cerca
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos neighborPos = pos.relative(direction);
                if (level.getBlockState(neighborPos).is(ModBlocks.ASTRAL_GRASS_BLOCK.get())) {
                    // Convertirse en grass
                    level.setBlockAndUpdate(pos,
                            ModBlocks.ASTRAL_GRASS_BLOCK.get().defaultBlockState());
                    break;
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}