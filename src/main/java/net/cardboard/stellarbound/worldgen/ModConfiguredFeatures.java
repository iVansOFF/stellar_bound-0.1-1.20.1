package net.cardboard.stellarbound.worldgen;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.registry.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_MOONSTONE_ORE_KEY = registerKey("moonstone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SUNSTONE_ORE_KEY = registerKey("sunstone_ore");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // Solo reemplaza deepslate (no piedra normal)
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        // Configuración para Moonstone (más raro, solo en deepslate)
        List<OreConfiguration.TargetBlockState> moonstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables,
                        ModBlocks.MOONSTONE.get().defaultBlockState())
        );

        // Configuración para Sunstone (más común, solo en deepslate)
        List<OreConfiguration.TargetBlockState> sunstoneOres = List.of(
                OreConfiguration.target(deepslateReplaceables,
                        ModBlocks.SUNSTONE.get().defaultBlockState())
        );

        register(context, OVERWORLD_MOONSTONE_ORE_KEY, Feature.ORE,
                new OreConfiguration(moonstoneOres, 7)); // Tamaño más pequeño para moonstone

        register(context, OVERWORLD_SUNSTONE_ORE_KEY, Feature.ORE,
                new OreConfiguration(sunstoneOres, 10)); // Tamaño más grande para sunstone
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, name));
    }

    private static <FC extends OreConfiguration, F extends Feature<FC>> void register(
            BootstapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}