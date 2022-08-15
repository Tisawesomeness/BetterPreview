package com.tisawesomeness.betterpreview.fabric.mc1191;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.fabric.BetterPreviewClient;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    // Update mod preview text on each chat field update
    @Inject(method = "updatePreviewer", at = @At("HEAD"), cancellable = true)
    private void updatePreviewer(String chatText, CallbackInfo ci) {
        String normalized = ((ChatScreen) (Object) this).normalize(chatText);
        BetterPreview.updateChatInput(normalized);
        ci.cancel(); // Don't request preview from server
    }

    // Replace the chat preview text with the one from the mod
    @Inject(method = "getPreviewScreenText", at = @At("HEAD"), cancellable = true)
    private void getPreviewScreenText(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(BetterPreviewClient.getPreview());
    }

    // Render preview only if BetterPreview allows
    @Inject(method = "shouldPreviewChat", at = @At("HEAD"), cancellable = true)
    private void shouldPreviewChat(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(BetterPreview.shouldDisplayPreview());
    }

}
