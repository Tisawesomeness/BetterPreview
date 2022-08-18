package com.tisawesomeness.betterpreview.fabric;

import com.tisawesomeness.betterpreview.fabric.network.ServerPacketListener;
import com.tisawesomeness.betterpreview.network.ByteBufs;
import com.tisawesomeness.betterpreview.network.Packet;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.minecraft.util.Identifier;

@Log4j2
public class BetterPreviewFabric implements ModInitializer {

    @Getter(lazy = true) private static final String version = computeVersion();

    @Override
    public void onInitialize() {
        ensureSubmodsLoaded();
        ServerPacketListener.init();
    }

    private static void ensureSubmodsLoaded() {
        if (FabricLoader.getInstance().getAllMods()
                .stream()
                .noneMatch(mod -> mod.getMetadata().getId().startsWith("betterpreview-"))) {
            throw new IllegalStateException("BetterPreview sub-mod didn't load correctly.");
        }
    }

    private static String computeVersion() {
        return FabricLoader.getInstance().getModContainer("betterpreview")
                .orElseThrow(() -> new IllegalStateException("BetterPreview mod container not found"))
                .getMetadata().getVersion().getFriendlyString();
    }

    public static void sendPacket(PacketSender sender, Packet packet) {
        var buf = PacketByteBufs.create();
        packet.write(buf);
        Identifier channel = FabricAudiences.toNative(packet.getChannel());

        if (log.isTraceEnabled()) {
            log.trace("Sending packet on channel {} with data {}", channel, ByteBufs.debug(buf));
        } else {
            log.debug("Sending packet on channel {}", channel);
        }

        sender.sendPacket(FabricAudiences.toNative(channel), buf);
    }

}
