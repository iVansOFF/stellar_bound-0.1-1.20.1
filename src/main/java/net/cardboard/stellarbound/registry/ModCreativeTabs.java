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
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.AETHERIUM_INGOT.get());
                                output.accept(ModItems.SOLANIUM_INGOT.get());
                                output.accept(ModItems.ITERIUM_ALLOY_INGOT.get());
                                output.accept(ModItems.WIMP_ESSENCE.get());
                                output.accept(ModItems.BRIGHT_ESSENCE.get());
                                output.accept(ModItems.MOON_SHARD.get());
                            })
                            .build()
            );
    public static final RegistryObject<CreativeModeTab> STELLARBOUND_MOBS =
            TABS.register("stellarbound_mobs", () ->
                    CreativeModeTab.builder()
                            .title(net.minecraft.network.chat.Component.literal("Stellarbound Mobs"))
                            .icon(() -> new ItemStack(ModItems.WIMP_SPAWN_EGG.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.WIMP_SPAWN_EGG.get());
                            })
                            .build()
            );
    public static final RegistryObject<CreativeModeTab> STELLARBOUND_BLOCKS =
            TABS.register("stellarbound_blocks", () ->
                    CreativeModeTab.builder()
                            .title(net.minecraft.network.chat.Component.literal("Stellarbound Blocks"))
                            .icon(() -> new ItemStack(ModItems.INFUSE_FORGERY.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.INFUSE_FORGERY.get());
                                output.accept(ModItems.MAGIC_WOOD_PLANK.get());
                                output.accept(ModItems.MOONSTONE.get());
                                output.accept(ModItems.SUNSTONE.get());
                            })
                            .build()
            );
}