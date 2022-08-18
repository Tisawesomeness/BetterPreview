package com.tisawesomeness.betterpreview.fabric.network;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.SupportInfo;
import com.tisawesomeness.betterpreview.SupportStatus;
import com.tisawesomeness.betterpreview.fabric.BetterPreviewClient;
import com.tisawesomeness.betterpreview.fabric.BetterPreviewFabric;
import com.tisawesomeness.betterpreview.format.FormatterStatus;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;
import com.tisawesomeness.betterpreview.network.ClientboundHello;
import com.tisawesomeness.betterpreview.network.ClientboundUpdate;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Log4j2
@Environment(EnvType.CLIENT)
public class ClientPacketListener {

    public static void init() {
        register(BetterPreview.HELLO_CHANNEL, ClientPacketListener::receiveHello);
        register(BetterPreview.UPDATE_CHANNEL, ClientPacketListener::receiveUpdate);
    }
    private static void register(Key channel, ClientPlayNetworking.PlayChannelHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(FabricAudiences.toNative(channel), handler);
    }

    private static void receiveHello(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var packet = new ClientboundHello(packetByteBuf);
        log.info("Received hello from server with version {}", packet.getServerVersion());
        var supportInfo = packet.getSupportInfo();
        log.info("Server supports {}", supportInfo.getRequestedVersion());
        if (supportInfo.getStatus().supportsPreviews()) {
            handleUpdate(packet.getUpdate());
        } else {
            displayUnsupportedMessage(supportInfo);
        }
    }

    private static void receiveUpdate(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var packet = new ClientboundUpdate(packetByteBuf);
        handleUpdate(packet.getUpdate());
    }

    private static void handleUpdate(FormatterUpdate update) {
        log.debug("Received update from server");
        if (update.getStatus() != FormatterStatus.OK) {
            log.info("Preview is disabled: {}", update.getStatus());
        }
        try {
            BetterPreview.setChatFormatter(update.getFormatter().orElse(null));
        } catch (IllegalArgumentException e) {
            log.error(e);
        }
    }
    private static void displayUnsupportedMessage(SupportInfo supportInfo) {
        if (supportInfo.getStatus() == SupportStatus.NO_SUPPORT) {
            MutableText message = Text.literal("BetterPreview is not supported on this server").formatted(Formatting.RED);
            addReasonIfNeeded(supportInfo, message);
            BetterPreviewClient.displayMessage(message);
        } else {
            MutableText outdatedMessage = Text.literal("Your version of BetterPreview (").formatted(Formatting.RED)
                    .append(Text.literal(BetterPreviewFabric.getVersion()).formatted(Formatting.GOLD))
                    .append(Text.literal(") is outdated").formatted(Formatting.RED));
            addReasonIfNeeded(supportInfo, outdatedMessage);
            BetterPreviewClient.displayMessage(outdatedMessage);

            Text updateMessage = Text.literal("Please update to ").formatted(Formatting.RED)
                    .append(Text.literal(supportInfo.getRequestedVersion()).formatted(Formatting.GOLD));
            BetterPreviewClient.displayMessage(updateMessage);
        }
    }
    private static void addReasonIfNeeded(SupportInfo supportInfo, MutableText message) {
        var reasonOpt = supportInfo.getMessage();
        if (reasonOpt.isPresent()) {
            message.append(Text.literal(": ").formatted(Formatting.RED))
                    .append(Text.literal(reasonOpt.get()).formatted(Formatting.DARK_RED));
        }
    }

}
