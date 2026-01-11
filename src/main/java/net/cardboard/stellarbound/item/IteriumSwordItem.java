package net.cardboard.stellarbound.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.NotNull;

public class IteriumSwordItem extends SwordItem {
    public IteriumSwordItem() {
        super(IteriumTier.ITERIUM, 3, -2.4F, new Properties());
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // Lógica adicional si la necesitas
        return super.hurtEnemy(stack, target, attacker);
    }
}