package com.tisawesomeness.betterpreview.fabric;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

@Log4j2
@Environment(EnvType.CLIENT)
public class BetterPreviewClient implements ClientModInitializer {

    private static final FabricClientAudiences AUDIENCE = FabricClientAudiences.of();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(BetterPreviewFabric.CHANNEL, this::receiveFormatter);
        ClientPlayConnectionEvents.DISCONNECT.register(BetterPreviewClient::disableFormatter);
    }
    private void receiveFormatter(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        try {
            BetterPreview.setChatFormatter(FormatterRegistry.read(packetByteBuf).orElse(null));
        } catch (IllegalArgumentException e) {
            log.error(e);
        }
    }
    private static void disableFormatter(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        BetterPreview.setChatFormatter(null);
    }

    public static Text getPreview() {
        return AUDIENCE.toNative(BetterPreview.getPreview());
    }

}
