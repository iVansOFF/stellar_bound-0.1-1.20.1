package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.screen.InfuseForgeryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Stellarbound.MOD_ID);

    public static final RegistryObject<MenuType<InfuseForgeryMenu>> INFUSE_FORGERY_MENU =
            MENUS.register("infuse_forgery_menu",
                    () -> IForgeMenuType.create(InfuseForgeryMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}