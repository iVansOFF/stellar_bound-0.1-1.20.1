package net.cardboard.stellarbound.network;

import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, "main"),
            () -> "1.0",
            "1.0"::equals,
            "1.0"::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        // Registrar GunActionPacket
        INSTANCE.messageBuilder(GunActionPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(GunActionPacket::new)
                .encoder(GunActionPacket::encode)
                .consumerMainThread(GunActionPacket::handle)
                .add();

        // Registrar ManaSyncPacket
        INSTANCE.messageBuilder(ManaSyncPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ManaSyncPacket::new)
                .encoder(ManaSyncPacket::encode)
                .consumerMainThread(ManaSyncPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}