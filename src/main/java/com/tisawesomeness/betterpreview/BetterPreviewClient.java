package com.tisawesomeness.betterpreview;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;
import com.tisawesomeness.betterpreview.format.NopFormatter;

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

    private static final ChatFormatter defaultFormatter = new NopFormatter();

    private static @Nullable ChatFormatter chatFormatter;
    private static String rawPreviewInput = "";

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(BetterPreview.CHANNEL, this::receiveFormatter);
        ClientPlayConnectionEvents.DISCONNECT.register(BetterPreviewClient::disableFormatter);
    }
    private void receiveFormatter(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        chatFormatter = FormatterRegistry.read(packetByteBuf).orElse(null);
    }
    private static void disableFormatter(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        chatFormatter = null;
    }

    public static void pushPreview(String rawInput) {
        rawPreviewInput = rawInput;
    }
    public static Text getPreview() {
        var formatter = chatFormatter == null ? defaultFormatter : chatFormatter;
        return formatter.format(rawPreviewInput);
    }

}
