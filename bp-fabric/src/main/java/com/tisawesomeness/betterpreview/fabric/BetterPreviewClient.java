package com.tisawesomeness.betterpreview.fabric;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.fabric.network.ClientPacketListener;
import com.tisawesomeness.betterpreview.network.ServerboundHello;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

@Log4j2
@Environment(EnvType.CLIENT)
public class BetterPreviewClient implements ClientModInitializer {

    private static final FabricClientAudiences AUDIENCE = FabricClientAudiences.of();
    private static final Text MESSAGE_PREFIX = AUDIENCE.toNative(BetterPreview.MESSAGE_PREFIX);

    @Override
    public void onInitializeClient() {
        ClientPacketListener.init();
        ClientPlayConnectionEvents.JOIN.register(BetterPreviewClient::sendHello);
        ClientPlayConnectionEvents.DISCONNECT.register(BetterPreviewClient::disableFormatter);
    }

    private static void sendHello(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        log.debug("Sending C2S hello");
        var packet = new ServerboundHello(BetterPreviewFabric.getVersion());
        BetterPreviewFabric.sendPacket(packetSender, packet);
    }

    private static void disableFormatter(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        BetterPreview.setChatFormatter(null);
    }

    public static void displayMessage(Text message) {
        Text prefixedMessage = MESSAGE_PREFIX.copy().append(message);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(prefixedMessage);
    }

    public static Text getPreview() {
        return AUDIENCE.toNative(BetterPreview.getPreview());
    }

}
