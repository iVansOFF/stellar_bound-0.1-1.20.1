package net.cardboard.stellarbound.network;

import net.cardboard.stellarbound.client.ClientManaData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaSyncPacket {
    private final float mana;
    private final float maxMana;

    public ManaSyncPacket(float mana, float maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public ManaSyncPacket(FriendlyByteBuf buf) {
        this.mana = buf.readFloat();
        this.maxMana = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(mana);
        buf.writeFloat(maxMana);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Actualizar datos en el cliente
            if (Minecraft.getInstance().player != null) {
                ClientManaData.setMana(mana);
                ClientManaData.setMaxMana(maxMana);
            }
        });
        return true;
    }
}