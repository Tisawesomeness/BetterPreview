package com.tisawesomeness.betterpreview.fabric;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.network.ByteBufs;
import com.tisawesomeness.betterpreview.network.ClientboundHello;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Log4j2
public class BetterPreviewFabric implements ModInitializer {

    public static final Identifier HELLO_CHANNEL = FabricAudiences.toNative(BetterPreview.HELLO_CHANNEL);
    public static final Identifier UPDATE_CHANNEL = FabricAudiences.toNative(BetterPreview.UPDATE_CHANNEL);

    @Override
    public void onInitialize() {
        ensureSubmodsLoaded();
        ServerPlayNetworking.registerGlobalReceiver(HELLO_CHANNEL, BetterPreviewFabric::receiveHello);
    }

    private static void ensureSubmodsLoaded() {
        if (FabricLoader.getInstance().getAllMods()
                .stream()
                .noneMatch(mod -> mod.getMetadata().getId().startsWith("betterpreview-"))) {
            throw new IllegalStateException("BetterPreview sub-mod didn't load correctly.");
        }
    }

    private static void receiveHello(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        String version = ByteBufs.readString(packetByteBuf);
        log.debug("Received C2S hello from {} with version {}", serverPlayerEntity.getGameProfile().getName(), version);
        sendHello(packetSender);
    }
    private static void sendHello(PacketSender packetSender) {
        var buf = PacketByteBufs.create();
        // Dummy formatter for now
        new ClientboundHello(getVersion(), null).write(buf);
        packetSender.sendPacket(HELLO_CHANNEL, buf);
    }

    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer("betterpreview")
                .orElseThrow(() -> new IllegalStateException("BetterPreview mod container not found"))
                .getMetadata().getVersion().getFriendlyString();
    }

}
