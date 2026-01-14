package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.item.*;
import net.cardboard.stellarbound.item.weapon.gun.FlintlockItem;
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

    // ========== ARMAS DE FUEGO ==========

    public static final RegistryObject<Item> FLINTLOCK = ITEMS.register("flintlock",
            FlintlockItem::new);

    // ========== DIAMANTE ==========
    public static final RegistryObject<Item> DIAMOND_SPEAR =
            ITEMS.register("diamond_spear",
                    () -> new SpearItem(
                            new Item.Properties().durability(500),
                            6.0,
                            -2.8,
                            1
                    ));

    // ========== HERRAMIENTAS ITERIUM ==========
    public static final RegistryObject<Item> ITERIUM_SWORD = ITEMS.register(
            "iterium_sword",
            IteriumSwordItem::new
    );

    public static final RegistryObject<Item> ITERIUM_PICKAXE = ITEMS.register(
            "iterium_pickaxe",
            IteriumPickaxeItem::new
    );

    public static final RegistryObject<Item> ITERIUM_AXE = ITEMS.register(
            "iterium_axe",
            IteriumAxeItem::new
    );

    public static final RegistryObject<Item> ITERIUM_HOE = ITEMS.register(
            "iterium_hoe",
            IteriumHoeItem::new
    );

    public static final RegistryObject<Item> ITERIUM_SHOVEL = ITEMS.register(
            "iterium_shovel",
            IteriumShovelItem::new
    );

    // ========== MATERIALES ==========
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

    public static final RegistryObject<Item> RESONANT_CORE = ITEMS.register(
            "resonant_core",
            () -> new Item(new Item.Properties())
    );

    // ========== SPAWN EGGS ==========
    public static final RegistryObject<Item> WIMP_SPAWN_EGG = ITEMS.register(
            "wimp_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.WIMP,
                    0xF59AC2,
                    0xF59AC285,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<Item> WISP_BELL_SPAWN_EGG = ITEMS.register(
            "wisp_bell_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.WISP_BELL,
                    0x004cb8,
                    0xFFB347,
                    new Item.Properties()
            )
    );

    // ========== BLOQUES ==========
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

    public static final RegistryObject<Item> INFUSE_FORGERY =
            ITEMS.register("infuse_forgery",
                    () -> new InfuseForgeryItem(
                            ModBlocks.INFUSE_FORGERY.get(),
                            new Item.Properties()
                    ));
}