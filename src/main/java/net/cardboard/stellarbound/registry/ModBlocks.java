package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.block.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Stellarbound.MOD_ID);

    public static final RegistryObject<Block> MAGIC_WOOD_PLANK =
            BLOCKS.register("magic_wood_plank",
                    () -> new Block(Block.Properties.copy(Blocks.OAK_PLANKS)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.WOOD)
                            .strength(2.0F, 6.0F)
                            .lightLevel(state -> 2) // 👈 luz real
                            .isValidSpawn((a,b,c,d) -> true)
                    ));
    public static final RegistryObject<Block> MOONSTONE =
            BLOCKS.register("moonstone",
                    () -> new SoulstoneBlock(Block.Properties.copy(Blocks.IRON_ORE)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.AMETHYST)
                            .strength(2.5F, 6.0F)
                            .lightLevel(state -> 5)
                            .isValidSpawn((a,b,c,d) -> true)
                    ));
    public static final RegistryObject<Block> SUNSTONE =
            BLOCKS.register("sunstone",
                    () -> new SunstoneBlock(Block.Properties.copy(Blocks.IRON_ORE)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.AMETHYST)
                            .strength(2.5F, 6.0F)
                            .lightLevel(state -> 8) // 👈 luz real
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> INFUSE_FORGERY = BLOCKS.register("infuse_forgery",
            () -> new InfuseForgeryBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(1F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final RegistryObject<Block> SOULSTONE =
            BLOCKS.register("soulstone",
                    () -> new SoulstoneBlock(Block.Properties.copy(Blocks.STONE)
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.STONE)
                            .strength(3F, 6.0F)
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> SOUL_COBBLESTONE =
            BLOCKS.register("soul_cobblestone",
                    () -> new SoulCobblestoneBlock(Block.Properties.copy(Blocks.STONE)
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.STONE)
                            .strength(3F, 6.0F)
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> ASTRAL_SOIL =
            BLOCKS.register("astral_soil",
                    () -> new AstralSoilBlock(Block.Properties.copy(Blocks.DIRT)
                            .mapColor(MapColor.DIRT)
                            .sound(SoundType.GRAVEL)
                            .strength(1F, 1.0F)
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> ASTRAL_GRASS_BLOCK =
            BLOCKS.register("astral_grass_block",
                    () -> new AstralGrassBlock(Block.Properties.copy(Blocks.GRASS_BLOCK)
                            .mapColor(MapColor.GRASS)
                            .sound(SoundType.GRASS)
                            .strength(1F, 1.0F)
                            .lightLevel(state -> 15)
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> SOULWOOD_LOG =
            BLOCKS.register("soulwood_log",
                    () -> new SoulwoodLogBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.NETHER_WOOD)
                            .strength(2F, 1.0F)
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> STRIPPED_SOULWOOD_LOG =
            BLOCKS.register("stripped_soulwood_log",
                    () -> new StrippedSoulwoodLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)
                            .mapColor(MapColor.WOOD)
                            .sound(SoundType.NETHER_WOOD)
                            .strength(2F, 1.0F)
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> LUMINOUS_SOULWOOD_LEAVES =
            BLOCKS.register("luminous_soulwood_leaves",
                    () -> new LuminousSoulwoodLeavesBlock(Block.Properties.copy(Blocks.ACACIA_LEAVES)
                            .mapColor(MapColor.GRASS)
                            .sound(SoundType.CHERRY_LEAVES)
                            .strength(0.5F, 1.0F)
                            .lightLevel(state -> 15)
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                            .isSuffocating((state, getter, pos) -> false)
                            .isViewBlocking((state, getter, pos) -> false)
                            .randomTicks()
                            .instabreak() // Similar a otras hojas
                            .requiresCorrectToolForDrops() // Se mina mejor con herramientas
                            .isRedstoneConductor((state, getter, pos) -> false) // No bloquea redstone
                    ));

    public static final RegistryObject<Block> SOULSONG_FLOWER = BLOCKS.register("soulsong_flower",
            () -> new SoulsongFlowerBlock(
                    MobEffects.REGENERATION, // Efecto al comer
                    200, // Duración en ticks (200 ticks = 10 segundos)
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.GRASS)
                            .instabreak()
                            .noCollission()
                            .lightLevel(state -> 12) // Brillo
                            .randomTicks() // Para que funcione randomTick()
                            .offsetType(BlockBehaviour.OffsetType.XZ)
                            .pushReaction(PushReaction.DESTROY)
            ));

    public static final RegistryObject<Block> SOULWOOD_SAPLING = BLOCKS.register("soulwood_sapling",
            () -> new SoulSapling(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.GRASS)
                            .instabreak()
                            .noCollission()
                            .lightLevel(state -> 12) // Brillo
                            .randomTicks() // Para que funcione randomTick()
                            .offsetType(BlockBehaviour.OffsetType.XZ)
                            .pushReaction(PushReaction.DESTROY)
            ));
}
