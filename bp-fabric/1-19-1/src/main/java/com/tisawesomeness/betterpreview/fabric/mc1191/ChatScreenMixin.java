package com.tisawesomeness.betterpreview.fabric.mc1191;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.fabric.BetterPreviewClient;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.ChatPreviewMode;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Shadow private ChatPreviewMode chatPreviewMode;

    // Ensure preview is updated while typing
    @Inject(method = "init", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/ChatScreen;chatPreviewMode:Lnet/minecraft/client/option/ChatPreviewMode;",
            ordinal = 0,
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
    ))
    private void init(CallbackInfo ci) {
        chatPreviewMode = ChatPreviewMode.LIVE;
    }

    // Update mod preview text on each chat field update
    @Inject(method = "updatePreviewer", at = @At("HEAD"))
    private void updatePreviewer(String chatText, CallbackInfo ci) {
        @SuppressWarnings("ConstantConditions")
        String normalized = ((ChatScreen) (Object) this).normalize(chatText);
        BetterPreview.updateChatInput(normalized);
    }

    // Replace the chat preview text with the one from the mod
    @Inject(method = "getPreviewScreenText", at = @At("HEAD"), cancellable = true)
    private void getPreviewScreenText(CallbackInfoReturnable<Text> cir) {
        var preview = BetterPreview.shouldDisplayPreview() ? BetterPreviewClient.getPreview() : Text.empty();
        cir.setReturnValue(preview);
    }

}
