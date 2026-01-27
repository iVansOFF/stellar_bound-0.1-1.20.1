package net.cardboard.stellarbound.worldgen;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.registry.ModBlocks;
import net.cardboard.stellarbound.worldgen.tree.custom.SoulwoodFoliagePlacer;
import net.cardboard.stellarbound.worldgen.tree.custom.SoulwoodTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModConfiguredFeatures {
    // Ores
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_MOONSTONE_ORE_KEY = registerKey("moonstone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SUNSTONE_ORE_KEY = registerKey("sunstone_ore");

    // Trees
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOULWOOD_KEY = registerKey("soulwood");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // ===== CONFIGURACIÓN DE MINERALES =====
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> moonstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables,
                        ModBlocks.MOONSTONE.get().defaultBlockState())
        );

        List<OreConfiguration.TargetBlockState> sunstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables,
                        ModBlocks.SUNSTONE.get().defaultBlockState())
        );

        register(context, OVERWORLD_MOONSTONE_ORE_KEY, Feature.ORE,
                new OreConfiguration(moonstoneOres, 7));

        register(context, OVERWORLD_SUNSTONE_ORE_KEY, Feature.ORE,
                new OreConfiguration(sunstoneOres, 10));

        // ===== CONFIGURACIÓN DE ÁRBOLES =====
        register(context, SOULWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SOULWOOD_LOG.get()),
                new SoulwoodTrunkPlacer(7, 2, 0), // altura base 7, variación 2
                BlockStateProvider.simple(ModBlocks.LUMINOUS_SOULWOOD_LEAVES.get()),
                new SoulwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 2)
        ).dirt(BlockStateProvider.simple(ModBlocks.ASTRAL_SOIL.get()))
                .build()); // ← .build() debe estar aquí, fuera del builder
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}