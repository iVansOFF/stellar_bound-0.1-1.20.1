package net.cardboard.stellarbound.worldgen.tree.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cardboard.stellarbound.worldgen.tree.ModFoliagePlacerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class SoulwoodFoliagePlacer extends FoliagePlacer {
    public static final Codec<SoulwoodFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
            foliagePlacerParts(instance).apply(instance, SoulwoodFoliagePlacer::new));

    public SoulwoodFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.SOULWOOD_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter, RandomSource random,
                                 TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment,
                                 int foliageHeight, int foliageRadius, int offset) {
        BlockPos pos = attachment.pos();

        // Forma de abeto: más ancho abajo, más estrecho arriba
        // El abeto tiene capas más espaciadas verticalmente

        // Capa inferior (más ancha) - nivel -2 desde el punto de follaje
        int bottomRadius = Math.max(2, foliageRadius + 1);
        this.placeLeavesRow(level, foliageSetter, random, config, pos.below(2),
                bottomRadius, 0, attachment.doubleTrunk());

        // Rellenar entre capas para más densidad
        this.placeLeavesRowWithHangingLeaves(level, foliageSetter, random, config, pos.below(1),
                bottomRadius - 1, 0, attachment.doubleTrunk(), 0.5f);

        // Capa media - nivel 0 (punto de follaje)
        this.placeLeavesRow(level, foliageSetter, random, config, pos,
                foliageRadius, 0, attachment.doubleTrunk());

        // Capa superior - nivel +1
        this.placeLeavesRow(level, foliageSetter, random, config, pos.above(1),
                Math.max(1, foliageRadius - 1), 0, attachment.doubleTrunk());

        // Punta del abeto - nivel +2
        this.placeLeavesRow(level, foliageSetter, random, config, pos.above(2),
                0, 0, attachment.doubleTrunk());

        // Agregar hojas adicionales en el tronco para más naturalidad
        addTrunkLeaves(level, foliageSetter, random, config, pos, maxFreeTreeHeight);
    }

    private void placeLeavesRowWithHangingLeaves(LevelSimulatedReader level, FoliageSetter foliageSetter,
                                                 RandomSource random, TreeConfiguration config,
                                                 BlockPos pos, int radius, int offset,
                                                 boolean doubleTrunk, float hangingChance) {
        this.placeLeavesRow(level, foliageSetter, random, config, pos, radius, offset, doubleTrunk);

        // Agregar algunas hojas colgantes (más naturales)
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) == radius || Math.abs(z) == radius) {
                    if (random.nextFloat() < hangingChance) {
                        BlockPos hangingPos = pos.offset(x, -1, z);
                        tryPlaceLeaf(level, foliageSetter, random, config, hangingPos);
                    }
                }
            }
        }
    }

    private void addTrunkLeaves(LevelSimulatedReader level, FoliageSetter foliageSetter,
                                RandomSource random, TreeConfiguration config,
                                BlockPos foliagePos, int treeHeight) {
        // Agregar algunas hojas adheridas al tronco (más realista)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i == 0 && j == 0) || random.nextFloat() > 0.4f) continue;

                // Hojas en el tronco principal
                tryPlaceLeaf(level, foliageSetter, random, config,
                        foliagePos.offset(i, -3, j));
                tryPlaceLeaf(level, foliageSetter, random, config,
                        foliagePos.offset(i, -4, j));
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeConfiguration config) {
        // Abetos tienen más altura de follaje (proporcional a la altura del árbol)
        int baseHeight = 4; // Altura base del follaje
        int extraHeight = random.nextInt(3); // 0-2 capas extra basado en altura del árbol

        // Árboles más altos tienen más capas de follaje
        if (treeHeight > 7) {
            extraHeight += 1;
        }

        return baseHeight + extraHeight;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ,
                                         int range, boolean large) {
        // Para forma de abeto: patrón más angular, menos circular
        int distance = Math.abs(localX) + Math.abs(localZ);

        // Centro siempre lleno
        if (distance == 0) return false;

        // Para forma de diamante/abeto
        if (distance > range) return true;

        // Más densidad en el centro, menos en los bordes
        float density = 1.0f - (distance / (float)(range * 2));

        // Los abetos son más densos en las capas inferiores
        if (localY < 0) density += 0.2f;

        // Omitir aleatoriamente basado en densidad
        return random.nextFloat() > density;
    }
}