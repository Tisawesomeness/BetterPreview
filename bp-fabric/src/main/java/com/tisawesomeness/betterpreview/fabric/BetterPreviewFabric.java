package com.tisawesomeness.betterpreview.fabric;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

public class BetterPreviewFabric implements ModInitializer {

    public static final Identifier CHANNEL = FabricAudiences.toNative(BetterPreview.CHANNEL);

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register(this::sendFormatterToClient);
    }
    private void sendFormatterToClient(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        var buf = PacketByteBufs.create();
        FormatterRegistry.write(buf, null);
        packetSender.sendPacket(CHANNEL, buf);
    }

}
