package com.tisawesomeness.betterpreview;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.ClassicFormatter;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.EnumSet;

public class BetterPreview implements ModInitializer {

    public static final Identifier CHANNEL = new Identifier("betterpreview", "formatter");

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register(this::sendFormatterToClient);
    }
    private void sendFormatterToClient(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        var buf = PacketByteBufs.create();
        FormatterRegistry.write(buf, buildFormatter());
        packetSender.sendPacket(CHANNEL, buf);
    }
    // Dummy example for now
    private ChatFormatter buildFormatter() {
        var allowedFormatting = EnumSet.range(Formatting.BLACK, Formatting.WHITE);
        allowedFormatting.add(Formatting.RESET);
        return new ClassicFormatter('&', allowedFormatting);
    }

}
