package net.cardboard.stellarbound.registry;

import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
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
                            .noOcclusion()
                            // 🔥 la clave
                            .isValidSpawn((a,b,c,d) -> true)
                    ));
}
