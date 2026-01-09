package net.cardboard.stellarbound;

import net.cardboard.stellarbound.client.renderer.WimpRenderer;
import net.cardboard.stellarbound.registry.ModBlocks;
import net.cardboard.stellarbound.registry.ModCreativeTabs;
import net.cardboard.stellarbound.registry.ModEntities;
import net.cardboard.stellarbound.registry.ModItems;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Stellarbound.MOD_ID)
public class Stellarbound
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "stellarbound";

    public Stellarbound(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);


        modEventBus.addListener(this::addCreative);
        ModCreativeTabs.TABS.register(modEventBus);

        ModEntities.ENTITIES.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Registrar el renderer del Wimp
            EntityRenderers.register(ModEntities.WIMP.get(), WimpRenderer::new);
        }
    }
}