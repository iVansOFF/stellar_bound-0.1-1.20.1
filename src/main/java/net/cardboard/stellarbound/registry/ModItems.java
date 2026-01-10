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
    public static final RegistryObject<Item> AETHERIUM_INGOT = ITEMS.register(
            "aetherium_ingot",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> SOLANIUM_INGOT = ITEMS.register(
            "solanium_ingot",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> BRIGHT_ESSENCE = ITEMS.register(
            "bright_essence",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> MOON_SHARD = ITEMS.register(
            "moon_shard",
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
                            new Item.Properties())
            );
    public static final RegistryObject<BlockItem> MOONSTONE =
            ITEMS.register("moonstone",
                    () -> new BlockItem(ModBlocks.MOONSTONE.get(),
                            new Item.Properties())
            );
    public static final RegistryObject<BlockItem> SUNSTONE =
            ITEMS.register("sunstone",
                    () -> new BlockItem(ModBlocks.SUNSTONE.get(),
                            new Item.Properties())
            );
    public static final RegistryObject<BlockItem> INFUSE_FORGERY =
            ITEMS.register("infuse_forgery",
                    () -> new BlockItem(ModBlocks.INFUSE_FORGERY.get(),
                            new Item.Properties())
            );
}