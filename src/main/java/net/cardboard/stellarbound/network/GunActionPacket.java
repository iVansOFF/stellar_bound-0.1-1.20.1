package net.cardboard.stellarbound.network;

import net.cardboard.stellarbound.item.weapon.gun.BaseGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GunActionPacket {

    public enum Action {
        SHOOT,
        RELOAD
    }

    private final Action action;
    private final boolean isMainHand;

    public GunActionPacket(Action action, boolean isMainHand) {
        this.action = action;
        this.isMainHand = isMainHand;
    }

    public GunActionPacket(FriendlyByteBuf buf) {
        this.action = buf.readEnum(Action.class);
        this.isMainHand = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(action);
        buf.writeBoolean(isMainHand);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            InteractionHand hand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);

            if (stack.getItem() instanceof BaseGunItem gun) {
                switch (action) {
                    case SHOOT -> gun.tryShoot(player, stack);
                    case RELOAD -> gun.startReload(player, stack);
                }
            }
        });
        context.setPacketHandled(true);
    }
}