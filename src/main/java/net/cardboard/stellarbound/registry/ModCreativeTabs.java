package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Stellarbound.MOD_ID);

    public static final RegistryObject<CreativeModeTab> STELLARBOUND_MATERIALS =
            TABS.register("stellarbound_materials", () ->
                    CreativeModeTab.builder()
                            .title(net.minecraft.network.chat.Component.literal("Stellarbound Materials"))
                            .icon(() -> new ItemStack(ModItems.ITERIUM_ALLOY_INGOT.get()))
                            .displayItems((parameters, output) -> output.accept(ModItems.ITERIUM_ALLOY_INGOT.get()))
                            .build()
            );
}