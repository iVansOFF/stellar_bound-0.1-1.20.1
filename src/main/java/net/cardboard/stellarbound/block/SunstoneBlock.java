package net.cardboard.stellarbound.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SunstoneBlock extends Block {

    public SunstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        // probabilidad (más alto = menos partículas)
        if (random.nextInt(4) != 0) return;

        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + 1.05;
        double z = pos.getZ() + random.nextDouble();

        level.addParticle(
                ParticleTypes.GLOW, // 👈 glow squid vibes
                x, y, z,
                0.0D, 0.02D, 0.0D // movimiento suave hacia arriba
        );
    }
}