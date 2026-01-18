package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.entity.BulletEntity;
import net.cardboard.stellarbound.entity.SkraeveEntity;
import net.cardboard.stellarbound.entity.WimpEntity;
import net.cardboard.stellarbound.entity.WispBellEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Stellarbound.MOD_ID);

    public static final RegistryObject<EntityType<WimpEntity>> WIMP =
            ENTITIES.register("wimp",
                    () -> EntityType.Builder.of(WimpEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.8f)
                            .build("wimp"));

    public static final RegistryObject<EntityType<WispBellEntity>> WISP_BELL =
            ENTITIES.register("wisp_bell",
                    () -> EntityType.Builder.of(WispBellEntity::new, MobCategory.CREATURE)
                            .sized(0.4f, 0.5f) // Más pequeño
                            .fireImmune() // Inmune al fuego
                            .build("wisp_bell"));

    public static final RegistryObject<EntityType<SkraeveEntity>> SKRAEVE =
            ENTITIES.register("skraeve",
                    () -> EntityType.Builder.of(SkraeveEntity::new, MobCategory.MONSTER)
                            .sized(2.0f, 2.0f) // size
                            .fireImmune() // Inmune al fuego
                            .build("skraeve"));

    public static final RegistryObject<EntityType<BulletEntity>> BULLET =
            ENTITIES.register("bullet",
                    () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("bullet"));
}