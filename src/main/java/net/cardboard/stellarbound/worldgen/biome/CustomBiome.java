package net.cardboard.stellarbound.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class CustomBiome {

    public static Biome createStarfieldsBiome(HolderGetter<PlacedFeature> placedFeatures,
                                              HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {

        // ===== EFECTOS ESPECIALES =====
        BiomeSpecialEffects.Builder effects = new BiomeSpecialEffects.Builder()
                .fogColor(0x1A1A2E)
                .waterColor(0x4A4AFF)
                .waterFogColor(0x2D2DFF)
                .skyColor(0x0D0D1A)
                .foliageColorOverride(0x8A2BE2)
                .grassColorOverride(0x9370DB)
                .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                .ambientParticle(new AmbientParticleSettings(ParticleTypes.END_ROD, 0.001F))
                .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
                .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0))
                .ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111))
                .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST));

        // ===== GENERACIÓN =====
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);

        // 1. Carvers básicos (Minecraft los necesita)
        BiomeDefaultFeatures.addDefaultCarversAndLakes(generation);

        // 2. Características VANILLA NECESARIAS (en orden correcto)
        BiomeDefaultFeatures.addDefaultCrystalFormations(generation);
        BiomeDefaultFeatures.addDefaultMonsterRoom(generation);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(generation);
        BiomeDefaultFeatures.addDefaultSprings(generation);
        BiomeDefaultFeatures.addSurfaceFreezing(generation);

        // ¡IMPORTANTE! NO añadas tus features aquí directamente
        // En su lugar, usa BiomeModifiers para añadirlas después
        // Esto evita el ciclo de dependencias

        // ===== MOBS =====
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder()
                .creatureGenerationProbability(0.07F);

        // Mob comunes
        spawns.addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 12, 4, 4));
        spawns.addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(EntityType.COW, 8, 4, 4));
        spawns.addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 10, 2, 3));

        // Mobs hostiles
        spawns.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 5, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.PHANTOM, 20, 1, 1));
        spawns.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.STRAY, 100, 4, 4));

        // ===== CONSTRUIR BIOMA =====
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.3F)    // Frío
                .downfall(0.2F)       // Seco
                .specialEffects(effects.build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(generation.build())
                .build();
    }
}