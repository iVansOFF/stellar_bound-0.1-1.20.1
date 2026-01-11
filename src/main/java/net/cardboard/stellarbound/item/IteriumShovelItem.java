package net.cardboard.stellarbound.item;

import net.minecraft.world.item.ShovelItem;
import org.jetbrains.annotations.NotNull;

public class IteriumShovelItem extends ShovelItem {
    public IteriumShovelItem() {
        super(IteriumTier.ITERIUM, 1.5F, -3.0F, new Properties());
    }
}