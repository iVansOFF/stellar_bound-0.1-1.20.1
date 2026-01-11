package net.cardboard.stellarbound;

import net.cardboard.stellarbound.worldgen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.cardboard.stellarbound.client.renderer.InfuseForgeryRenderer;
import net.cardboard.stellarbound.client.renderer.WispBellRenderer;
import net.cardboard.stellarbound.entity.WimpEntity;
import net.cardboard.stellarbound.client.renderer.WimpRenderer;
import net.cardboard.stellarbound.entity.WispBellEntity;
import net.cardboard.stellarbound.recipe.ModRecipes;
import net.cardboard.stellarbound.registry.*;
import net.cardboard.stellarbound.screen.InfuseForgeryScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
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
import net.minecraftforge.client.event.EntityRenderersEvent;

import java.util.Set;

@Mod(Stellarbound.MOD_ID)
public class Stellarbound {
    public static final String MOD_ID = "stellarbound";

    public Stellarbound(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        ModCreativeTabs.TABS.register(modEventBus);

        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ModEntities.ENTITIES.register(modEventBus);

        ModMenuTypes.MENUS.register(modEventBus);

        modEventBus.addListener(this::onGatherData);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SuppressWarnings("deprecation")
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Registrar Wimp
            SpawnPlacements.register(
                    ModEntities.WIMP.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    WimpEntity::checkWimpSpawnRules
            );

            // Registrar WispBell
            SpawnPlacements.register(
                    ModEntities.WISP_BELL.get(),
                    SpawnPlacements.Type.NO_RESTRICTIONS,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    WispBellEntity::checkWispSpawnRules
            );
        });
    }

    private void onGatherData(final GatherDataEvent event) {
        // Si quieres usar Data Generators (opcional pero recomendado)
        if (event.includeServer()) {
            event.getGenerator().addProvider(true, new DatapackBuiltinEntriesProvider(
                    event.getGenerator().getPackOutput(),
                    event.getLookupProvider(),
                    new RegistrySetBuilder()
                            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
                            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
                            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap),
                    Set.of(MOD_ID)
            ));
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.WIMP.get(), WimpRenderer::new);
            EntityRenderers.register(ModEntities.WISP_BELL.get(), WispBellRenderer::new);

            // Registra el screen de la GUI
            MenuScreens.register(ModMenuTypes.INFUSE_FORGERY_MENU.get(), InfuseForgeryScreen::new);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                    ModBlockEntities.INFUSE_FORGERY.get(),
                    context -> new InfuseForgeryRenderer()
            );
        }
    }
}