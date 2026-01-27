package net.cardboard.stellarbound.block;

import net.cardboard.stellarbound.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

public class SoulsongFlowerBlock extends FlowerBlock {

    // Forma del hitbox (más pequeña que un bloque completo)
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 14.0D, 12.0D);

    public SoulsongFlowerBlock(MobEffect effect, int duration, Properties properties) {
        super(effect, duration, properties);
    }

    // Constructor alternativo si no quieres pasar efecto y duración
    public SoulsongFlowerBlock(Properties properties) {
        super(MobEffects.REGENERATION, 200, properties); // 10 segundos de regeneración
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // MÉTODO CLAVE: Sobrescribir para restringir dónde se puede colocar
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        // Solo se puede colocar en ASTRAL_GRASS_BLOCK
        return state.is(ModBlocks.ASTRAL_GRASS_BLOCK.get());
    }

    // Opcional: También permitir en bloques similares si quieres
    /*
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.ASTRAL_GRASS_BLOCK.get()) ||
               state.is(ModBlocks.ASTRAL_SOIL.get()) ||
               state.is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK) ||
               state.is(net.minecraft.world.level.block.Blocks.DIRT) ||
               state.is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }
    */

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        // Partículas de alma ascendentes
        if (random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 0.8;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;

            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x, y, z,
                    0, 0.02, 0);

            // Partículas musicales ocasionales
            if (random.nextInt(20) == 0) {
                level.addParticle(ParticleTypes.NOTE,
                        x, y + 0.2, z,
                        random.nextDouble(), 0, random.nextDouble());
            }
        }

        // Sonido ambiental ocasional (susurros)
        if (random.nextInt(200) == 0 && level.isClientSide) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(),
                    SoundSource.BLOCKS,
                    0.1F,
                    1.2F + random.nextFloat() * 0.2F,
                    false);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Ocasionalmente cura entidades cercanas
        if (random.nextInt(100) == 0) {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                            new net.minecraft.world.phys.AABB(pos).inflate(3.0))
                    .forEach(entity -> {
                        if (entity.getHealth() < entity.getMaxHealth()) {
                            entity.heal(1.0F);

                            // Enviar partículas a todos los clientes
                            level.sendParticles(ParticleTypes.HEART,
                                    entity.getX(),
                                    entity.getY() + entity.getBbHeight() / 2,
                                    entity.getZ(),
                                    5,
                                    0.3, 0.5, 0.3,
                                    0.1);
                        }
                    });
        }
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        // Puedes crear un PlantType personalizado si quieres
        return PlantType.PLAINS;
    }

    // Métodos adicionales para mejor comportamiento

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