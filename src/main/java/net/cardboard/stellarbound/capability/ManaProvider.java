package net.cardboard.stellarbound.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerMana> MANA = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerMana mana = null;
    private final LazyOptional<PlayerMana> optional = LazyOptional.of(this::createMana);

    private PlayerMana createMana() {
        if (mana == null) {
            mana = new PlayerMana();
        }
        return mana;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == MANA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createMana().serializeNBT().getAllKeys().forEach(key ->
                tag.put(key, createMana().serializeNBT().get(key))
        );
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createMana().deserializeNBT(nbt);
    }
}