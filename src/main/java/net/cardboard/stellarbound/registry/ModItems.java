package net.cardboard.stellarbound.registry;

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
}