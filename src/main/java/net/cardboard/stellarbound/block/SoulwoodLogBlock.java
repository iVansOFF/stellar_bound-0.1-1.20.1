package net.cardboard.stellarbound.block;

import net.cardboard.stellarbound.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SoulwoodLogBlock extends RotatedPillarBlock {
    // Propiedad para la dirección del eje
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public SoulwoodLogBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Verificar si el jugador tiene un hacha
        if (itemStack.getItem() instanceof AxeItem) {
            // Sonido de pelar
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (!level.isClientSide) {
                // Obtener el eje actual
                Direction.Axis axis = state.getValue(AXIS);

                // Crear el bloque pelado con la misma orientación
                BlockState strippedState = ModBlocks.STRIPPED_SOULWOOD_LOG.get().defaultBlockState()
                        .setValue(AXIS, axis);

                // Reemplazar el bloque
                level.setBlock(pos, strippedState, 11);

                // Dañar el hacha
                itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));

                // Otorgar logro/avance si quieres
                // player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
    }
}