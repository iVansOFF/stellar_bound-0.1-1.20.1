package net.cardboard.stellarbound.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.cardboard.stellarbound.registry.ModBlocks;

public class AstralGrassBlock extends SpreadingSnowyDirtBlock {

    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    public AstralGrassBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWY);
    }

    // MÉTODO CLAVE: Esto hace que se convierta en tierra cuando se coloca un bloque sólido encima
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP) {
            // Si se coloca un bloque sólido encima, convertir en Astral Soil
            if (neighborState.isSolidRender(level, neighborPos)) {
                return ModBlocks.ASTRAL_SOIL.get().defaultBlockState();
            }
            return state.setValue(SNOWY, isSnowySetting(neighborState));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private static boolean isSnowySetting(BlockState state) {
        return state.is(Blocks.SNOW) && state.getValue(SnowLayerBlock.LAYERS) == 1;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Verificar si tiene suficiente luz
        if (!canBeGrass(state, level, pos)) {
            level.setBlockAndUpdate(pos, ModBlocks.ASTRAL_SOIL.get().defaultBlockState());
        } else {
            // Propagación a Astral Soil cercano
            if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                trySpread(state, level, pos, random);
            }
        }
    }

    private void trySpread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        for(int i = 0; i < 4; ++i) {
            BlockPos targetPos = pos.offset(
                    random.nextInt(3) - 1,
                    random.nextInt(5) - 3,
                    random.nextInt(3) - 1
            );

            if (level.getBlockState(targetPos).is(ModBlocks.ASTRAL_SOIL.get()) &&
                    canPropagate(state, level, targetPos)) {

                BlockState newState = this.defaultBlockState()
                        .setValue(SNOWY, level.getBlockState(targetPos.above()).is(Blocks.SNOW));

                level.setBlockAndUpdate(targetPos, newState);
            }
        }
    }

    private static boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // Permitir nieve delgada
        if (aboveState.is(Blocks.SNOW) && aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        }

        // Necesita al menos nivel 4 de luz (similar al grass vanilla)
        int light = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, abovePos);
        return light >= 4 && !aboveState.isSolidRender(level, abovePos);
    }

    private static boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
        return canBeGrass(state, level, pos);
    }

    // Para compatibilidad con pala
    @Override
    public boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) {
        return false;
    }
}