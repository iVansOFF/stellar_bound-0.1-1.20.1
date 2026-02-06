package net.cardboard.stellarbound.worldgen;

import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> SOULWOOD_PLACED_KEY = registerKey("soulwood_placed");
    public static final ResourceKey<PlacedFeature> SOULSONG_FLOWER_PLACED_KEY = registerKey("soulsong_flower_placed");
    public static final ResourceKey<PlacedFeature> STARFIELDS_MOONSTONE_PLACED_KEY = registerKey("starfields_moonstone_placed");
    public static final ResourceKey<PlacedFeature> STARFIELDS_SUNSTONE_PLACED_KEY = registerKey("starfields_sunstone_placed");
    public static final ResourceKey<PlacedFeature> OVERWORLD_MOONSTONE_PLACED_KEY = registerKey("overworld_moonstone_placed");
    public static final ResourceKey<PlacedFeature> OVERWORLD_SUNSTONE_PLACED_KEY = registerKey("overworld_sunstone_placed");

    // ❌ ELIMINADAS
    // public static final ResourceKey<PlacedFeature> REPLACE_SURFACE_PLACED_KEY = registerKey("replace_surface_placed");
    // public static final ResourceKey<PlacedFeature> REPLACE_STONE_PLACED_KEY = registerKey("replace_stone_placed");

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, name));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // ❌ ELIMINADAS
        // context.register(REPLACE_SURFACE_PLACED_KEY, createReplaceSurfacePlaced(...));
        // context.register(REPLACE_STONE_PLACED_KEY, createReplaceStonePlaced(...));

        context.register(SOULWOOD_PLACED_KEY, createSoulwoodPlaced(configuredFeatures.getOrThrow(ModConfiguredFeatures.SOULWOOD_KEY)));
        context.register(SOULSONG_FLOWER_PLACED_KEY, createSoulsongFlowerPlaced(configuredFeatures.getOrThrow(ModConfiguredFeatures.SOULSONG_FLOWER_KEY)));
        context.register(STARFIELDS_MOONSTONE_PLACED_KEY, createStarfieldsMoonstonePlaced(configuredFeatures.getOrThrow(ModConfiguredFeatures.STARFIELDS_MOONSTONE_ORE_KEY)));
        context.register(STARFIELDS_SUNSTONE_PLACED_KEY, createStarfieldsSunstonePlaced(configuredFeatures.getOrThrow(ModConfiguredFeatures.STARFIELDS_SUNSTONE_ORE_KEY)));
        context.register(OVERWORLD_MOONSTONE_PLACED_KEY, createOverworldMoonstonePlaced(configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_MOONSTONE_KEY)));
        context.register(OVERWORLD_SUNSTONE_PLACED_KEY, createOverworldSunstonePlaced(configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_SUNSTONE_KEY)));
    }

    // ... resto de métodos sin cambios (createSoulwoodPlaced, etc.)
    public static PlacedFeature createSoulwoodPlaced(Holder<ConfiguredFeature<?, ?>> feature) {
        return new PlacedFeature(feature,
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE
                )
        );
    }

    public static PlacedFeature createSoulsongFlowerPlaced(Holder<ConfiguredFeature<?, ?>> feature) {
        return new PlacedFeature(feature,
                List.of(
                        RarityFilter.onAverageOnceEvery(32),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE
                )
        );
    }

    public static PlacedFeature createStarfieldsMoonstonePlaced(Holder<ConfiguredFeature<?, ?>> feature) {
        return new PlacedFeature(feature,
                List.of(
                        CountPlacement.of(20),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(80)
                        )
                )
        );
    }

    public static PlacedFeature createStarfieldsSunstonePlaced(Holder<ConfiguredFeature<?, ?>> feature) {
        return new PlacedFeature(feature,
                List.of(
                        CountPlacement.of(15),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-32),
                                VerticalAnchor.absolute(96)
                        )
                )
        );
    }

    public static PlacedFeature createOverworldMoonstonePlaced(Holder<ConfiguredFeature<?, ?>> feature) {
        return new PlacedFeature(feature,
                List.of(
                        CountPlacement.of(8),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(30)
                        )
                )
        );
    }

    public static PlacedFeature createOverworldSunstonePlaced(Holder<ConfiguredFeature<?, ?>> feature) {
        return new PlacedFeature(feature,
                List.of(
                        CountPlacement.of(6),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(
                                VerticalAnchor.absolute(-32),
                                VerticalAnchor.absolute(60)
                        )
                )
        );
    }

    // ❌ ELIMINADAS
    // public static PlacedFeature createReplaceSurfacePlaced(...)
    // public static PlacedFeature createReplaceStonePlaced(...)
}