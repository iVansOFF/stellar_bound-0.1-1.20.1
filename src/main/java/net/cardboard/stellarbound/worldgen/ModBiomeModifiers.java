package net.cardboard.stellarbound.worldgen;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.worldgen.biome.ModBiomes;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomeModifiers {

    // ❌ ELIMINADAS - Ya no se necesitan
    // public static final ResourceKey<BiomeModifier> REPLACE_STARFIELDS_SURFACE = createKey("replace_starfields_surface");

    public static final ResourceKey<BiomeModifier> ADD_OVERWORLD_MOONSTONE = createKey("add_overworld_moonstone");
    public static final ResourceKey<BiomeModifier> ADD_OVERWORLD_SUNSTONE = createKey("add_overworld_sunstone");
    public static final ResourceKey<BiomeModifier> ADD_STARFIELDS_MOONSTONE = createKey("add_starfields_moonstone");
    public static final ResourceKey<BiomeModifier> ADD_STARFIELDS_SUNSTONE = createKey("add_starfields_sunstone");
    public static final ResourceKey<BiomeModifier> ADD_STARFIELDS_VEGETATION = createKey("add_starfields_vegetation");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var biomes = context.lookup(Registries.BIOME);
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        // ❌ ELIMINADA - SurfaceRules lo maneja automáticamente
        // context.register(REPLACE_STARFIELDS_SURFACE, ...);

        // Ores para Overworld
        context.register(ADD_OVERWORLD_MOONSTONE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_MOONSTONE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ADD_OVERWORLD_SUNSTONE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_SUNSTONE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        // Ores específicos para Starfields
        context.register(ADD_STARFIELDS_MOONSTONE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.STARFIELDS_KEY)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.STARFIELDS_MOONSTONE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ADD_STARFIELDS_SUNSTONE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.STARFIELDS_KEY)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.STARFIELDS_SUNSTONE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        // Vegetación para Starfields
        context.register(ADD_STARFIELDS_VEGETATION, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.STARFIELDS_KEY)),
                HolderSet.direct(
                        placedFeatures.getOrThrow(ModPlacedFeatures.SOULWOOD_PLACED_KEY),
                        placedFeatures.getOrThrow(ModPlacedFeatures.SOULSONG_FLOWER_PLACED_KEY)
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }

    private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, name));
    }
}