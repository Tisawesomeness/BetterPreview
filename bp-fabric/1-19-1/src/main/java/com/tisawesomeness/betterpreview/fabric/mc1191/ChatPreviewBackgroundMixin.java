package com.tisawesomeness.betterpreview.fabric.mc1191;

import com.tisawesomeness.betterpreview.BetterPreview;

import net.minecraft.client.gui.screen.ChatPreviewBackground;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatPreviewBackground.class)
public class ChatPreviewBackgroundMixin {

    // Render preview only if BetterPreview allows
    @Inject(method = "computeRenderData", at = @At("HEAD"), cancellable = true)
    private void computeRenderData(long currentTime, Text previewText, CallbackInfoReturnable<ChatPreviewBackground.RenderData> cir) {
        if (!BetterPreview.shouldDisplayPreview()) {
            cir.setReturnValue(ChatPreviewBackground.RenderData.EMPTY);
        }
    }

}
