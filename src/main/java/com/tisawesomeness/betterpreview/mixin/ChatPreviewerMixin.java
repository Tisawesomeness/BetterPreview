package com.tisawesomeness.betterpreview.mixin;

import com.tisawesomeness.betterpreview.client.BetterPreviewClient;

import net.minecraft.client.network.ChatPreviewer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatPreviewer.class)
public class ChatPreviewerMixin {
    @Inject(method = "getPreviewText", at = @At("HEAD"), cancellable = true)
    private void getPreviewText(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(BetterPreviewClient.getPreview());
    }
    @Inject(method = "shouldRenderPreview", at = @At("HEAD"), cancellable = true)
    private void shouldRenderPreview(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
