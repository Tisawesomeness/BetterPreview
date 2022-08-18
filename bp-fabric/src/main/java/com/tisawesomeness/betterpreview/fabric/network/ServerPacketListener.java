package com.tisawesomeness.betterpreview.fabric.network;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.SupportInfo;
import com.tisawesomeness.betterpreview.fabric.BetterPreviewFabric;
import com.tisawesomeness.betterpreview.format.FormatterStatus;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;
import com.tisawesomeness.betterpreview.network.ClientboundHello;
import com.tisawesomeness.betterpreview.network.ServerboundHello;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Log4j2
public class ServerPacketListener {

    public static void init() {
        register(BetterPreview.HELLO_CHANNEL, ServerPacketListener::receiveHello);
    }
    private static void register(Key channel, ServerPlayNetworking.PlayChannelHandler handler) {
        ServerPlayNetworking.registerGlobalReceiver(FabricAudiences.toNative(channel), handler);
    }

    private static void receiveHello(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var packet = new ServerboundHello(packetByteBuf);
        String clientVersion = packet.getClientVersion();
        log.debug("Received C2S hello from {} with version {}", serverPlayerEntity.getGameProfile().getName(), clientVersion);
        sendHello(packetSender);
    }
    private static void sendHello(PacketSender packetSender) {
        String serverVersion = BetterPreviewFabric.getVersion();
        var supportInfo = SupportInfo.supported(serverVersion);
        var update = FormatterUpdate.disabled(FormatterStatus.SERVER_DISABLED);
        var packet = ClientboundHello.withUpdate(serverVersion, supportInfo, update);
        BetterPreviewFabric.sendPacket(packetSender, packet);
    }

}
