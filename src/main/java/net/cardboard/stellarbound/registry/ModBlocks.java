package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                    ));
    public static final RegistryObject<Block> MOONSTONE =
            BLOCKS.register("moonstone",
                    () -> new SoulstoneBlock(Block.Properties.copy(Blocks.IRON_ORE)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.AMETHYST)
                            .strength(2.5F, 6.0F)
                            .lightLevel(state -> 5)
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                    ));
    public static final RegistryObject<Block> SUNSTONE =
            BLOCKS.register("sunstone",
                    () -> new SunstoneBlock(Block.Properties.copy(Blocks.IRON_ORE)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .sound(SoundType.AMETHYST)
                            .strength(2.5F, 6.0F)
                            .lightLevel(state -> 8) // 👈 luz real
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> INFUSE_FORGERY = BLOCKS.register("infuse_forgery",
            () -> new InfuseForgeryBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final RegistryObject<Block> SOULSTONE =
            BLOCKS.register("soulstone",
                    () -> new SoulstoneBlock(Block.Properties.copy(Blocks.STONE)
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.STONE)
                            .strength(3F, 6.0F)
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> SOUL_COBBLESTONE =
            BLOCKS.register("soul_cobblestone",
                    () -> new SoulCobblestoneBlock(Block.Properties.copy(Blocks.STONE)
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.STONE)
                            .strength(3F, 6.0F)
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> ASTRAL_SOIL =
            BLOCKS.register("astral_soil",
                    () -> new AstralSoilBlock(Block.Properties.copy(Blocks.DIRT)
                            .mapColor(MapColor.DIRT)
                            .sound(SoundType.GRAVEL)
                            .strength(1F, 1.0F)
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                    ));

    public static final RegistryObject<Block> ASTRAL_GRASS_BLOCK =
            BLOCKS.register("astral_grass_block",
                    () -> new AstralGrassBlock(Block.Properties.copy(Blocks.GRASS_BLOCK)
                            .mapColor(MapColor.GRASS)
                            .sound(SoundType.GRASS)
                            .strength(1F, 1.0F)
                            .lightLevel(state -> 8)
                            .noOcclusion()
                            .isValidSpawn((a,b,c,d) -> true)
                    ));
}
