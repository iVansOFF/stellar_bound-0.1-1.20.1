package net.cardboard.stellarbound.worldgen.biome.surface;

import net.cardboard.stellarbound.registry.ModBlocks;
import net.cardboard.stellarbound.worldgen.biome.ModBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRules {
    // Bloques del mod
    private static final SurfaceRules.RuleSource ASTRAL_GRASS = makeStateRule(ModBlocks.ASTRAL_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource ASTRAL_SOIL = makeStateRule(ModBlocks.ASTRAL_SOIL.get());
    private static final SurfaceRules.RuleSource SOULSTONE = makeStateRule(ModBlocks.SOULSTONE.get());

    // Bloques vanilla por defecto
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);

    public static SurfaceRules.RuleSource makeRules() {
        // Condición: nivel del agua
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);

        // Superficie normal (vanilla) - para otros biomas
        SurfaceRules.RuleSource grassSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK),
                DIRT
        );

        return SurfaceRules.sequence(
                // ⭐ REGLAS PARA STARFIELDS (se evalúan primero)
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.STARFIELDS_KEY),
                        SurfaceRules.sequence(
                                // Piso (superficie): Astral Grass
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(isAtOrAboveWaterLevel, ASTRAL_GRASS),
                                                ASTRAL_SOIL
                                        )
                                ),
                                // Subsuelo (debajo de la superficie): Soulstone
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SOULSTONE),
                                // Techo de cuevas: Soulstone
                                SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SOULSTONE)
                        )
                ),

                // ⭐ Superficie por defecto para TODOS los demás biomas
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassSurface)
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}