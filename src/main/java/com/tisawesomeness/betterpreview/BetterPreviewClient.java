package com.tisawesomeness.betterpreview;

import com.tisawesomeness.betterpreview.format.ChatFormatter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class BetterPreviewClient implements ClientModInitializer {

    private static @Nullable ChatFormatter chatFormatter;
    private static String rawPreviewInput = "";

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(BetterPreview.CHANNEL, this::receiveFormatter);
        ClientPlayConnectionEvents.DISCONNECT.register(BetterPreviewClient::disableFormatter);
    }
    private void receiveFormatter(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        chatFormatter = new ChatFormatter(packetByteBuf);
    }
    private static void disableFormatter(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        chatFormatter = null;
    }

    public static void pushPreview(String rawInput) {
        rawPreviewInput = rawInput;
    }
    public static Text getPreview() {
        return chatFormatter == null ? Text.literal(rawPreviewInput) : chatFormatter.format(rawPreviewInput);
    }

}
