package net.cardboard.stellarbound.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerMana implements INBTSerializable<CompoundTag> {
    private float mana;
    private float maxMana = 100.0f;
    private float manaRegen = 1.0f;

    public float getMana() {
        return mana;
    }

    public float getMaxMana() {
        return maxMana;
    }

    public void setMana(float mana) {
        this.mana = Math.min(Math.max(mana, 0), maxMana);
    }

    public void addMana(float amount) {
        setMana(this.mana + amount);
    }

    public boolean consumeMana(float amount) {
        if (mana >= amount) {
            mana -= amount;
            return true;
        }
        return false;
    }

    public void setMaxMana(float maxMana) {
        this.maxMana = maxMana;
        if (mana > maxMana) {
            mana = maxMana;
        }
    }

    public float getManaRegen() {
        return manaRegen;
    }

    public void setManaRegen(float regen) {
        this.manaRegen = regen;
    }

    public void tickRegen() {
        if (mana < maxMana) {
            mana = Math.min(mana + manaRegen, maxMana);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("mana", mana);
        tag.putFloat("maxMana", maxMana);
        tag.putFloat("manaRegen", manaRegen);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        mana = tag.getFloat("mana");
        maxMana = tag.getFloat("maxMana");
        manaRegen = tag.getFloat("manaRegen");
    }
}