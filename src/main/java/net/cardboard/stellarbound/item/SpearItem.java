package net.cardboard.stellarbound.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class SpearItem extends Item {

    private final Multimap<Attribute, AttributeModifier> attributes;

    public SpearItem(Properties properties, double damage, double speed, double reach) {
        super(properties);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        BASE_ATTACK_DAMAGE_UUID,
                        "Spear damage",
                        damage,
                        AttributeModifier.Operation.ADDITION
                )
        );

        builder.put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        BASE_ATTACK_SPEED_UUID,
                        "Spear speed",
                        speed,
                        AttributeModifier.Operation.ADDITION
                )
        );

        builder.put(
                ForgeMod.ENTITY_REACH.get(),
                new AttributeModifier(
                        UUID.randomUUID(),
                        "Spear reach",
                        reach,
                        AttributeModifier.Operation.ADDITION
                )
        );

        this.attributes = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND
                ? this.attributes
                : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            stack.hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

}
