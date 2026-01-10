package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.InfuseForgeryBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Stellarbound.MOD_ID);

    public static final RegistryObject<BlockEntityType<InfuseForgeryBlockEntity>> INFUSE_FORGERY =
            BLOCK_ENTITIES.register("infuse_forgery",
                    () -> BlockEntityType.Builder.of(
                            InfuseForgeryBlockEntity::new,
                            ModBlocks.INFUSE_FORGERY.get()
                    ).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
