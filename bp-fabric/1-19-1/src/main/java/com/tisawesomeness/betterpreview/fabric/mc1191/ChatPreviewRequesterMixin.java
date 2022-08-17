package com.tisawesomeness.betterpreview.fabric.mc1191;

import net.minecraft.client.network.ChatPreviewRequester;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatPreviewRequester.class)
public class ChatPreviewRequesterMixin {

    // Don't request preview from server
    @Inject(method = "tryRequest", at = @At("HEAD"), cancellable = true)
    private void tryRequest(String message, long currentTime, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
