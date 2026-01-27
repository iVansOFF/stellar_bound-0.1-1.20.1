package net.cardboard.stellarbound.worldgen.tree;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.worldgen.tree.custom.SoulwoodTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTrunkPlacerTypes {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Stellarbound.MOD_ID);

    public static final RegistryObject<TrunkPlacerType<SoulwoodTrunkPlacer>> SOULWOOD_TRUNK_PLACER =
            TRUNK_PLACERS.register("soulwood_trunk_placer",
                    () -> new TrunkPlacerType<>(SoulwoodTrunkPlacer.CODEC));

    public static void register(IEventBus eventBus) {
        TRUNK_PLACERS.register(eventBus);
    }
}