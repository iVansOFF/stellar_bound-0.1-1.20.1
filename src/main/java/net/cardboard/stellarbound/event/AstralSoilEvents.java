package net.cardboard.stellarbound.event;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Stellarbound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AstralSoilEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        // Solo en servidor
        if (level.isClientSide()) {
            return;
        }

        // No jugadores
        if (entity instanceof Player) {
            return;
        }

        // Obtener posición del bloque debajo
        BlockPos entityPos = entity.blockPosition();
        BlockPos soilPos = entityPos.below();

        BlockState soilState = level.getBlockState(soilPos);

        // Verificar si es Astral Soil
        if (soilState.is(ModBlocks.ASTRAL_SOIL.get())) {
            // Verificar condiciones
            BlockPos abovePos = soilPos.above();
            int lightLevel = level.getMaxLocalRawBrightness(abovePos);
            boolean isSolidAbove = level.getBlockState(abovePos).isSolidRender(level, abovePos);

            if (lightLevel >= 9 && !isSolidAbove) {
                // Convertir a Astral Grass Block
                level.setBlockAndUpdate(soilPos,
                        ModBlocks.ASTRAL_GRASS_BLOCK.get().defaultBlockState());

                // Efecto de sonido
                level.playSound(null, soilPos, SoundEvents.GRASS_PLACE,
                        SoundSource.BLOCKS, 0.8F, 1.2F);

                // Partículas (opcional)
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.GLOW,
                            soilPos.getX() + 0.5,
                            soilPos.getY() + 1.0,
                            soilPos.getZ() + 0.5,
                            10, // cantidad
                            0.5, 0.5, 0.5, // spread
                            0.0 // velocidad
                    );
                }
            }
        }
    }
}