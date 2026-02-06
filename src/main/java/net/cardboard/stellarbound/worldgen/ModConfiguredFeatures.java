package net.cardboard.stellarbound.worldgen;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.registry.ModBlocks;
import net.cardboard.stellarbound.worldgen.tree.custom.SoulwoodFoliagePlacer;
import net.cardboard.stellarbound.worldgen.tree.custom.SoulwoodTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SOULWOOD_KEY = registerKey("soulwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOULSONG_FLOWER_KEY = registerKey("soulsong_flower");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STARFIELDS_MOONSTONE_ORE_KEY = registerKey("starfields_moonstone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STARFIELDS_SUNSTONE_ORE_KEY = registerKey("starfields_sunstone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_MOONSTONE_KEY = registerKey("overworld_moonstone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SUNSTONE_KEY = registerKey("overworld_sunstone");

    // ❌ ELIMINADAS - Ya no se usan
    // public static final ResourceKey<ConfiguredFeature<?, ?>> REPLACE_SURFACE_KEY = registerKey("replace_surface");
    // public static final ResourceKey<ConfiguredFeature<?, ?>> REPLACE_STONE_KEY = registerKey("replace_stone");

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, name));
    }

    public static void bootstrap(net.minecraft.data.worldgen.BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(SOULWOOD_KEY, createSoulwoodFeature());
        context.register(SOULSONG_FLOWER_KEY, createSoulsongFlowerFeature());
        context.register(STARFIELDS_MOONSTONE_ORE_KEY, createStarfieldsMoonstoneOre());
        context.register(STARFIELDS_SUNSTONE_ORE_KEY, createStarfieldsSunstoneOre());
        context.register(OVERWORLD_MOONSTONE_KEY, createOverworldMoonstoneOre());
        context.register(OVERWORLD_SUNSTONE_KEY, createOverworldSunstoneOre());

        // ❌ ELIMINADAS - Ya no se registran
        // context.register(REPLACE_SURFACE_KEY, ModSurfaceReplacements.createSurfaceReplacement());
        // context.register(REPLACE_STONE_KEY, ModSurfaceReplacements.createStoneReplacement());
    }

    // ... resto de métodos sin cambios
    private static ConfiguredFeature<?, ?> createSoulwoodFeature() {
        return new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(ModBlocks.SOULWOOD_LOG.get()),
                        new SoulwoodTrunkPlacer(7, 2, 0),
                        BlockStateProvider.simple(ModBlocks.LUMINOUS_SOULWOOD_LEAVES.get()),
                        new SoulwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).dirt(BlockStateProvider.simple(ModBlocks.ASTRAL_SOIL.get()))
                        .ignoreVines()
                        .build());
    }

    private static ConfiguredFeature<?, ?> createSoulsongFlowerFeature() {
        return new ConfiguredFeature<>(Feature.FLOWER,
                new RandomPatchConfiguration(
                        32, 6, 2,
                        PlacementUtils.onlyWhenEmpty(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(ModBlocks.SOULSONG_FLOWER.get())
                                )
                        )
                ));
    }

    private static ConfiguredFeature<?, ?> createStarfieldsMoonstoneOre() {
        var deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        var stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        var moonstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables, ModBlocks.MOONSTONE.get().defaultBlockState()),
                OreConfiguration.target(stoneReplaceables, ModBlocks.MOONSTONE.get().defaultBlockState())
        );
        return new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(moonstoneOres, 9, 0.5f));
    }

    private static ConfiguredFeature<?, ?> createStarfieldsSunstoneOre() {
        var deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        var stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        var sunstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables, ModBlocks.SUNSTONE.get().defaultBlockState()),
                OreConfiguration.target(stoneReplaceables, ModBlocks.SUNSTONE.get().defaultBlockState())
        );
        return new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(sunstoneOres, 12, 0.7f));
    }

    private static ConfiguredFeature<?, ?> createOverworldMoonstoneOre() {
        var deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        var stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        var moonstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables, ModBlocks.MOONSTONE.get().defaultBlockState()),
                OreConfiguration.target(stoneReplaceables, ModBlocks.MOONSTONE.get().defaultBlockState())
        );
        return new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(moonstoneOres, 7, 0.5f));
    }

    private static ConfiguredFeature<?, ?> createOverworldSunstoneOre() {
        var deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        var stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        var sunstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables, ModBlocks.SUNSTONE.get().defaultBlockState()),
                OreConfiguration.target(stoneReplaceables, ModBlocks.SUNSTONE.get().defaultBlockState())
        );
        return new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(sunstoneOres, 10, 0.7f));
    }
}