package net.cardboard.stellarbound.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Stellarbound.MOD_ID);

    public static final RegistryObject<Item> ITERIUM_ALLOY_INGOT = ITEMS.register(
            "iterium_alloy_ingot",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> WIMP_ESSENCE = ITEMS.register(
            "wimp_essence",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> WIMP_SPAWN_EGG = ITEMS.register(
            "wimp_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.WIMP,
                    0xF59AC2, // rosado wimp
                    0xFFCCE5, // rosado claro
                    new Item.Properties()
            )
    );
    public static final RegistryObject<BlockItem> MAGIC_WOOD_PLANK =
            ITEMS.register("magic_wood_plank",
                    () -> new BlockItem(ModBlocks.MAGIC_WOOD_PLANK.get(),
                            new Item.Properties()));
}