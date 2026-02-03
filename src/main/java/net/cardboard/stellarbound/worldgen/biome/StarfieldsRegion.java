package net.cardboard.stellarbound.worldgen.biome;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class StarfieldsRegion extends Region {

    public StarfieldsRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<com.mojang.datafixers.util.Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // REDUCE el rango de los parámetros para hacer el bioma más pequeño

        // Parámetros del clima para Starfields (AJUSTADOS):
        // Temperature: -0.5 a 0.2 (frío) → Reducido el rango
        // Humidity: -0.5 a 0.2 (seco) → Reducido el rango
        // Continentalness: -0.2 a 0.5 (más pequeño)
        // Erosion: 0.3 a 0.7 (rango más estrecho)
        // Depth: 0.0 (superficie)
        // Weirdness: -0.5 a 0.5 (centrado)
        // Offset: 0.0

        this.addBiome(
                mapper,
                Climate.parameters(
                        Climate.Parameter.span(-0.3F, 0.1F),    // temperatura (rango más pequeño)
                        Climate.Parameter.span(-0.3F, 0.1F),    // humedad (rango más pequeño)
                        Climate.Parameter.span(-0.2F, 0.5F),    // continentalidad (más limitada)
                        Climate.Parameter.span(0.3F, 0.7F),     // erosión (rango más estrecho)
                        Climate.Parameter.point(0.0F),          // profundidad (superficie)
                        Climate.Parameter.span(-0.5F, 0.5F),    // rareza (centrado, menos extremo)
                        0.0F                                    // offset
                ),
                ModBiomes.STARFIELDS_KEY
        );
    }
}