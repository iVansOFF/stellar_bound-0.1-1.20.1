package net.cardboard.stellarbound.item;

import net.cardboard.stellarbound.registry.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import org.jetbrains.annotations.NotNull;

public class IteriumTier {
    public static final Tier ITERIUM = new ForgeTier(
            2, // Nivel equivalente a hierro
            250, // Durabilidad (hierro tiene 250)
            6.0F, // Velocidad de minería (hierro tiene 6.0)
            2.0F, // Daño base (hierro tiene 2.0)
            14, // Encantabilidad (hierro tiene 14)
            BlockTags.NEEDS_IRON_TOOL, // Requiere nivel de hierro para minar
            () -> Ingredient.of(ModItems.ITERIUM_ALLOY_INGOT.get()) // Material de reparación
    );
}