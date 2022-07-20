package com.tisawesomeness.betterpreview.mixin;

import com.tisawesomeness.betterpreview.BetterPreviewClient;

import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    // Update mod preview text on each chat field update
    @Inject(method = "updatePreviewer", at = @At("HEAD"), cancellable = true)
    private void getPreviewText(String chatText, CallbackInfo ci) {
        String normalized = ((ChatScreen) (Object) this).normalize(chatText);
        BetterPreviewClient.pushPreview(normalized);
        ci.cancel(); // Don't request preview from server
    }

}
