package net.cardboard.stellarbound;

import net.cardboard.stellarbound.client.ModKeyBindings;
import net.cardboard.stellarbound.client.hud.GunDebugOverlay;
import net.cardboard.stellarbound.client.hud.GunHudOverlay;
import net.cardboard.stellarbound.client.hud.ManaOverlay;
import net.cardboard.stellarbound.client.renderer.*;
import net.cardboard.stellarbound.network.ModPackets;
import net.cardboard.stellarbound.worldgen.biome.ModBiomes;
import net.cardboard.stellarbound.worldgen.biome.StarfieldsRegion;
import net.cardboard.stellarbound.worldgen.tree.ModFoliagePlacerTypes;
import net.cardboard.stellarbound.worldgen.tree.ModTrunkPlacerTypes;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.cardboard.stellarbound.worldgen.*;
import net.cardboard.stellarbound.entity.WimpEntity;
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
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrablender.api.Regions;
import java.util.Set;

@Mod(Stellarbound.MOD_ID)
public class Stellarbound {
    public static final String MOD_ID = "stellarbound";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public Stellarbound(FMLJavaModLoadingContext context) {
        ModPackets.register();

        IEventBus modEventBus = context.getModEventBus();

        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);

        // Registrar todos los registros
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);

        modEventBus.addListener(this::onGatherData);

        // Registrar tree placers
        ModTrunkPlacerTypes.register(modEventBus);
        ModFoliagePlacerTypes.register(modEventBus);

        // Registrar región de TerraBlender
        modEventBus.addListener(this::registerTerrablender);
    }

    private void registerTerrablender(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Registrar la región de Starfields con TerraBlender
            Regions.register(new StarfieldsRegion(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "starfields_region"),
                    2 // Peso de generación
            ));
        });
    }

    private void onGatherData(final GatherDataEvent event) {
        LOGGER.info("=== GENERANDO DATAPACKS ===");
        if (event.includeServer()) {
            try {
                event.getGenerator().addProvider(true, new DatapackBuiltinEntriesProvider(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        new RegistrySetBuilder()
                                .add(Registries.CONFIGURED_FEATURE, context -> {
                                    LOGGER.info("Registrando ConfiguredFeatures...");
                                    ModConfiguredFeatures.bootstrap(context);
                                })
                                .add(Registries.PLACED_FEATURE, context -> {
                                    LOGGER.info("Registrando PlacedFeatures...");
                                    ModPlacedFeatures.bootstrap(context);
                                })
                                .add(Registries.BIOME, context -> {
                                    LOGGER.info("Registrando Biomes...");
                                    ModBiomes.bootstrap(context);
                                })
                                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                                    LOGGER.info("Registrando BiomeModifiers...");
                                    ModBiomeModifiers.bootstrap(context);
                                }),
                        Set.of(MOD_ID)
                ));
                LOGGER.info("=== DATAPACKS GENERADOS CORRECTAMENTE ===");
            } catch (Exception e) {
                LOGGER.error("ERROR al generar datapacks: ", e);
                throw e;
            }
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SuppressWarnings("deprecation")
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(
                    ModEntities.WIMP.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    WimpEntity::checkWimpSpawnRules
            );

            SpawnPlacements.register(
                    ModEntities.WISP_BELL.get(),
                    SpawnPlacements.Type.NO_RESTRICTIONS,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    WispBellEntity::checkWispSpawnRules
            );
        });
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
            LOGGER.info("=== STELLARBOUND CLIENT SETUP ===");

            EntityRenderers.register(ModEntities.WIMP.get(), WimpRenderer::new);
            EntityRenderers.register(ModEntities.WISP_BELL.get(), WispBellRenderer::new);
            EntityRenderers.register(ModEntities.SKRAEVE.get(), SkraeveRenderer::new);
            EntityRenderers.register(ModEntities.BULLET.get(), BulletRenderer::new);
            EntityRenderers.register(ModEntities.FIREBALL.get(), FireballRenderer::new);

            MenuScreens.register(ModMenuTypes.INFUSE_FORGERY_MENU.get(), InfuseForgeryScreen::new);

            LOGGER.info("=== CLIENT SETUP COMPLETADO ===");
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                    ModBlockEntities.INFUSE_FORGERY.get(),
                    context -> new InfuseForgeryRenderer()
            );
        }

        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("gun_hud", new GunHudOverlay());
            event.registerAboveAll("gun_debug", new GunDebugOverlay());
            event.registerAboveAll("mana_bar", new ManaOverlay());
        }

        @SubscribeEvent
        public static void registerKeyBindings(net.minecraftforge.client.event.RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.RELOAD_KEY);
            event.register(ModKeyBindings.SHOOT_KEY);
        }
    }
}