package net.cardboard.stellarbound.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SwordClassItem extends SwordItem {

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributes;

    public SwordClassItem(Tier tier, int attackDamage, float attackSpeed, double reach, Item.Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);

        this.lazyAttributes = Lazy.of(() -> {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
            builder.put(
                    ForgeMod.ENTITY_REACH.get(),
                    new AttributeModifier(
                            UUID.randomUUID(),
                            "Weapon modifier",
                            reach,
                            AttributeModifier.Operation.ADDITION
                    )
            );
            return builder.build();
        });
    }

    @Override
    @NotNull
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND
                ? this.lazyAttributes.get()
                : super.getDefaultAttributeModifiers(slot);
    }
}