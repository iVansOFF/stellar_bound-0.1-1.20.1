package net.cardboard.stellarbound.worldgen;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.registry.ModBlocks;
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
    // Ores
    public static final ResourceKey<PlacedFeature> MOONSTONE_ORE_PLACED_KEY = registerKey("moonstone_ore_placed");
    public static final ResourceKey<PlacedFeature> SUNSTONE_ORE_PLACED_KEY = registerKey("sunstone_ore_placed");

    // Trees
    public static final ResourceKey<PlacedFeature> SOULWOOD_PLACED_KEY = registerKey("soulwood_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // ===== MINERALES =====
        register(context, MOONSTONE_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_MOONSTONE_ORE_KEY),
                ModOrePlacement.commonOrePlacement(1,
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(0)
                        )));

        register(context, SUNSTONE_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_SUNSTONE_ORE_KEY),
                ModOrePlacement.commonOrePlacement(2,
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-32),
                                VerticalAnchor.absolute(32)
                        )));

        // ===== ÁRBOLES =====
        // ===== ÁRBOLES =====
        register(context, SOULWOOD_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.SOULWOOD_KEY),
                List.of(
                        // Esto es para generación natural en el mundo
                        // Si solo quieres que crezca de saplings, quita esto
                        // O usa RarityFilter si quieres que aparezca naturalmente
                        // InSquarePlacement.spread(),
                        // SurfaceWaterDepthFilter.forMaxDepth(0),
                        // BiomeFilter.biome()
                ));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}