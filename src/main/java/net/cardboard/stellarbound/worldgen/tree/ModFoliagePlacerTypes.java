package net.cardboard.stellarbound.worldgen.tree;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.worldgen.tree.custom.SoulwoodFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFoliagePlacerTypes {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, Stellarbound.MOD_ID);

    public static final RegistryObject<FoliagePlacerType<SoulwoodFoliagePlacer>> SOULWOOD_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("soulwood_foliage_placer",
                    () -> new FoliagePlacerType<>(SoulwoodFoliagePlacer.CODEC));

    public static void register(IEventBus eventBus) {
        FOLIAGE_PLACERS.register(eventBus);
    }
}